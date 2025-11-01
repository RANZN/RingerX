package com.ranjan.ringerx.app.ui.ringer_setting.components

import android.media.AudioManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ModeRadioButtonGroup(
    selectedMode: Int,
    onModeSelected: (Int) -> Unit
) {
    val modes = listOf(
        "Ring" to AudioManager.RINGER_MODE_NORMAL,
        "Vibrate" to AudioManager.RINGER_MODE_VIBRATE,
        "Silent" to AudioManager.RINGER_MODE_SILENT
    )

    // Arrange the radio buttons in a Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        modes.forEach { (name, mode) ->
            Row(
                // Make the whole Row clickable
                modifier = Modifier
                    .selectable(
                        selected = (selectedMode == mode),
                        onClick = { onModeSelected(mode) }
                    )
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedMode == mode),
                    onClick = { onModeSelected(mode) }
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}