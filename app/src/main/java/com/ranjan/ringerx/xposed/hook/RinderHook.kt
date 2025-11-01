package com.ranjan.ringerx.xposed.hook

import android.media.AudioManager
import android.util.Log
import com.ranjan.ringerx.app.utils.PACKAGE_NAME
import com.ranjan.ringerx.app.utils.Prefs.Companion.KEY_EVENTS_JSON
import com.ranjan.ringerx.app.utils.Prefs.Companion.PREFS_FILE
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import org.json.JSONArray
import java.util.Calendar

class RingerHook : IXposedHookLoadPackage {

    private var cachedEvents: List<RingerEventFromHook>? = null
    private var lastSeenJson: String? = null

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        if (lpparam.packageName != "com.android.systemui") return

        runCatching {
            XposedHelpers.findAndHookMethod(
                "com.android.systemui.statusbar.policy.Clock",
                lpparam.classLoader,
                "onTimeChanged",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        handleClockTick(param)
                    }
                }
            )
        }.onFailure {
            Log.e("RingerApp", "Failed to hook Clock.onTimeChanged: ${it.message}")
        }
    }

    private fun handleClockTick(param: XC_MethodHook.MethodHookParam) {

        // 1. Get the current time
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val audioManager = param.thisObject as AudioManager

        // 2. Check for settings changes
        try {
            val prefs = XSharedPreferences(PACKAGE_NAME, PREFS_FILE)
            val newJson = prefs.getString(KEY_EVENTS_JSON, "[]") ?: "[]"

            // This is the optimization:
            // Only re-parse the JSON if the string is different.
            if (newJson != lastSeenJson) {
                Log.i("RingerApp", "Settings changed! Re-parsing event list.")
                cachedEvents = parseEvents(newJson)
                lastSeenJson = newJson
            }
            // If the JSON is the same, we do nothing and use the fast cache.

        } catch (e: Exception) {
            Log.e("RingerApp", "Error reading or parsing prefs: ${e.message}")
            cachedEvents = emptyList() // Clear cache on error
        }

        // 3. Check for a match using the fast in-memory cache
        val event = cachedEvents?.find { it.hour == currentHour && it.minute == currentMinute }

        if (event != null) {
            // 4. Take Action!
            if (audioManager.ringerMode != event.mode) {
                audioManager.ringerMode = event.mode
                Log.i("RingerApp", "Match found in cache! Setting ringer mode to ${event.mode}")
            }
        }
    }

    private fun parseEvents(jsonString: String): List<RingerEventFromHook> {
        val list = mutableListOf<RingerEventFromHook>()
        try {
            val events = JSONArray(jsonString)
            for (i in 0 until events.length()) {
                val event = events.getJSONObject(i)
                list.add(
                    RingerEventFromHook(
                        hour = event.getInt("hour"),
                        minute = event.getInt("minute"),
                        mode = event.getInt("mode")
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("RingerApp", "Error parsing JSON: ${e.message}")
        }
        return list
    }

}

private data class RingerEventFromHook(val hour: Int, val minute: Int, val mode: Int)
