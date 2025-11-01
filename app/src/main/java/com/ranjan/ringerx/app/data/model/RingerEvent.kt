package com.ranjan.ringerx.app.data.model

import android.annotation.SuppressLint
import android.media.AudioManager
import kotlinx.serialization.Serializable

@Serializable
data class RingerEvent(
    val id: Long,
    val hour: Int,
    val minute: Int,
    val mode: Int
) {
    /**
     * Helper function to get a readable mode name for the UI.
     */
    fun getModeName(): String {
        return when (mode) {
            AudioManager.RINGER_MODE_NORMAL -> "Sound"
            AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
            AudioManager.RINGER_MODE_SILENT -> "Silent (Mute)"
            else -> "Unknown"
        }
    }

    /**
     * Helper function to get a readable time string for the UI (e.g., "9:05 AM").
     */
    fun getTimeString(): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = if (hour == 0 || hour == 12) 12 else hour % 12
        val displayMinute = minute.toString().padStart(2, '0')
        return "$displayHour:$displayMinute $amPm"
    }
}