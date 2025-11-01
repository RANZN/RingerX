package com.ranjan.ringerx.app.utils


import android.content.Context
import android.content.SharedPreferences
import com.ranjan.ringerx.app.data.model.RingerEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.core.content.edit

class Prefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    companion object {
        const val KEY_EVENTS_JSON = "ringer_events_json"
        const val PREFS_FILE = "ringer_prefs"
    }

    /**
     * This is the function you asked for.
     * It reads the JSON string from storage and converts it back
     * into a List<RingerEvent>.
     */
    fun loadEvents(): List<RingerEvent> {
        // 1. Read the saved JSON string. Default to null if not found.
        val jsonString = prefs.getString(KEY_EVENTS_JSON, null)

        return if (jsonString != null) {
            try {
                // 2. Use the Json library to parse the string into a List
                Json.decodeFromString(jsonString)
            } catch (e: Exception) {
                // 3. If parsing fails (e.g., corrupted data), return an empty list
                e.printStackTrace()
                listOf()
            }
        } else {
            // 4. If no string was saved, return an empty list
            listOf()
        }
    }

    /**
     * This is the companion function to save the list.
     * It converts the list into a JSON string and saves it.
     */
    fun saveEvents(events: List<RingerEvent>) {
        // 1. Convert the list of events into one long JSON string
        val jsonString = Json.encodeToString(events)

        // 2. Save that string into SharedPreferences
        prefs.edit { putString(KEY_EVENTS_JSON, jsonString) }
    }
}