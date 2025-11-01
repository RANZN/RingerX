package com.ranjan.ringerx.xposed.hook

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.util.Log
import android.view.View
import com.ranjan.ringerx.app.data.model.RingerEvent
import com.ranjan.ringerx.app.utils.PACKAGE_NAME
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlinx.serialization.json.Json
import java.util.Calendar

class RingerHook : IXposedHookLoadPackage {

    private var cachedEvents: List<RingerEvent> = emptyList()
    private var lastCheckedMinute: Int = -1

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        if (lpparam.packageName != "com.android.systemui") return

        Log.i("RingerApp", "RingerX Hook loaded into SystemUI.")
        runCatching {
            val clockClass = XposedHelpers.findClass(
                "com.android.systemui.statusbar.policy.Clock",
                lpparam.classLoader
            )
            val methodsToHook = clockClass.declaredMethods
                .filter { it.name == "updateClock" }

            methodsToHook.forEach { method ->
                XposedBridge.hookMethod(method, object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        handleClockTick(param)
                    }
                })
            }
            if (methodsToHook.isNotEmpty()) {
                Log.i("RingerApp", "Hooked ${methodsToHook.size} updateClock method(s).")
            } else {
                Log.w("RingerApp", "No updateClock method found in Clock class.")
            }
        }.onFailure {
            Log.e("RingerApp", "Failed to hook Clock.onTimeChanged: $it")
        }
    }

    private fun handleClockTick(param: XC_MethodHook.MethodHookParam) {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        if (minute == lastCheckedMinute) return
        lastCheckedMinute = minute

        val context = (param.thisObject as? View)?.context ?: return
        // Load + only update cache when data actually changes
        val events = loadEventsFromProvider(context)
        if (events != cachedEvents) {
            cachedEvents = events
        }

        // Try to find matching event directly
        val match = cachedEvents.find { it.hour == hour && it.minute == minute } ?: return

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        // Exit DND only when setting ringer to Normal/Vibrate to avoid "Silent = DND" issue.
        // This prevents overriding manual DND activation by the user.
        if (match.mode != AudioManager.RINGER_MODE_SILENT && notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            Log.i("RingerApp", "Exited DND to set ringer mode.")
        }

        // Apply mode only if needed
        if (audioManager.ringerMode != match.mode) {
            audioManager.ringerMode = match.mode
            Log.i("RingerApp", "Ringer mode changed to ${match.mode}")
        }
    }


    fun loadEventsFromProvider(context: Context): List<RingerEvent> {
        val uri = Uri.parse("content://$PACKAGE_NAME.prefs")

        val cursor = context.contentResolver.query(uri, null, null, null, null)
            ?: return emptyList()

        val json = cursor.use {
            if (it.moveToFirst()) it.getString(0) else "[]"
        }

        return try {
            Json.decodeFromString(json)
        } catch (_: Exception) {
            emptyList()
        }
    }

}
