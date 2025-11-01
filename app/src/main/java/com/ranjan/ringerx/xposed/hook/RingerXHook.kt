package com.ranjan.ringerx.xposed.hook

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.ranjan.ringerx.app.data.model.RingerEvent
import com.ranjan.ringerx.app.utils.PACKAGE_NAME
import com.ranjan.ringerx.app.utils.SYSTEMUI_PACKAGE_NAME
import com.ranjan.ringerx.app.utils.TAG
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlinx.serialization.json.Json
import java.util.Calendar

class RingerXHook : IXposedHookLoadPackage {

    @Volatile
    private var cachedEvents: List<RingerEvent> = emptyList()
    private var isInitialized = false
    private val timeTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_TIME_TICK) checkAndApplyRingerMode(context)
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != SYSTEMUI_PACKAGE_NAME) return

        XposedBridge.log("$TAG: RingerX Hook loading into SystemUI.")

        runCatching {
            XposedHelpers.findAndHookMethod(
                "android.content.ContextWrapper",
                lpparam.classLoader,
                "registerReceiver",
                BroadcastReceiver::class.java,
                IntentFilter::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        // We only need to initialize once.
                        if (isInitialized) return

                        val ctx = param.thisObject as? Context ?: return
                        initialize(ctx.applicationContext)
                        isInitialized = true
                    }
                }
            )
        }.onFailure {
            XposedBridge.log("$TAG: Failed to hook ContextWrapper.registerReceiver: $it")
        }
    }

    private fun initialize(context: Context) {
        XposedBridge.log("$TAG: Initializing RingerX hooks.")

        // Initial load of events
        cachedEvents = loadEventsFromProvider(context)
        updateBroadcastReceiver(context)

        // Register a ContentObserver to listen for changes in our app's preferences.
        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                XposedBridge.log("$TAG: Detected a change in events, reloading.")
                cachedEvents = loadEventsFromProvider(context)
                updateBroadcastReceiver(context)
                // After loading new events, check if a ringer mode change is needed right now.
                // This handles the case where an event is created for the current minute.
                checkAndApplyRingerMode(context)
            }
        }
        val uri = Uri.parse("content://$PACKAGE_NAME.prefs")
        context.contentResolver.registerContentObserver(uri, true, contentObserver)

        XposedBridge.log("$TAG: Initialization complete. Listening for content changes.")
    }

    /**
     * Registers or unregisters the TIME_TICK broadcast receiver based on whether there are
     * any scheduled events. This prevents the system from waking up every minute if no
     * events are scheduled.
     */
    private fun updateBroadcastReceiver(context: Context) {
        if (cachedEvents.isNotEmpty()) {
            XposedBridge.log("$TAG: Events scheduled, registering TIME_TICK receiver.")
            context.registerReceiver(timeTickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        } else {
            XposedBridge.log("$TAG: No events scheduled, unregistering TIME_TICK receiver.")
            runCatching { context.unregisterReceiver(timeTickReceiver) }
        }
    }

    private fun checkAndApplyRingerMode(context: Context) {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)

        // Try to find matching event directly
        val match = cachedEvents.find { it.hour == hour && it.minute == minute } ?: return

        XposedBridge.log("$TAG: Matched event found for $hour:$minute. Applying mode ${match.mode}.")
        val audioManager =
            context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        // Exit DND only when setting ringer to Normal/Vibrate to avoid "Silent = DND" issue.
        // This prevents overriding manual DND activation by the user.
        if (match.mode != AudioManager.RINGER_MODE_SILENT && notificationManager.isNotificationPolicyAccessGranted && notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            XposedBridge.log("$TAG: Exited DND to set ringer mode.")
        }

        // Apply mode only if needed
        if (audioManager.ringerMode != match.mode) {
            audioManager.ringerMode = match.mode
            XposedBridge.log("$TAG: Ringer mode changed to ${match.mode}")
        }
    }

    fun loadEventsFromProvider(context: Context): List<RingerEvent> {
        val uri = Uri.parse("content://$PACKAGE_NAME.prefs")
        val newEvents: List<RingerEvent> = runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val json = if (cursor.moveToFirst()) cursor.getString(0) else "[]"
                Json.decodeFromString<List<RingerEvent>>(json)
            }
        }.onFailure {
            XposedBridge.log("$TAG: Failed to load events from provider: $it")
        }.getOrNull() ?: emptyList()

        XposedBridge.log("$TAG: Successfully loaded ${newEvents.size} events.")
        return newEvents
    }

}
