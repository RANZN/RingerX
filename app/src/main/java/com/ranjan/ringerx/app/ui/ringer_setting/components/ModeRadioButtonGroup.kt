package com.ranjan.ringerx.app.ui.ringer_setting.components

import android.media.AudioManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        modes.forEachIndexed { index, (label, mode) ->
            SegmentedButton(
                selected = selectedMode == mode,
                onClick = { onModeSelected(mode) },
                shape = when (index) {
                    0 -> SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = modes.size
                    ) // auto rounded for first
                    modes.lastIndex -> SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = modes.size
                    ) // last item
                    else -> SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = modes.size
                    ) // middle items
                }
            ) {
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}