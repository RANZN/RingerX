package com.ranjan.ringerx.app.utils


import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import com.ranjan.ringerx.app.data.model.RingerEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Prefs(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    companion object {
        const val KEY_EVENTS_JSON = "ringer_events_json"
        const val PREFS_FILE = "ringer_prefs"
    }

    fun loadEvents(): List<RingerEvent> {
        val jsonString = prefs.getString(KEY_EVENTS_JSON, null) ?: return emptyList()
        return runCatching {
            Json.decodeFromString<List<RingerEvent>>(jsonString)
        }.onFailure {
            it.printStackTrace()
        }.getOrDefault(emptyList())
    }

    /**
     * This is the companion function to save the list.
     * It converts the list into a JSON string and saves it.
     */
    fun saveEvents(events: List<RingerEvent>) {
        val jsonString = Json.encodeToString(events)

        prefs.edit { putString(KEY_EVENTS_JSON, jsonString) }

        val uri = Uri.parse("content://$PACKAGE_NAME.prefs")

        // Notify the system that the data at this URI has changed.
        context.contentResolver.notifyChange(uri, null)

    }
}