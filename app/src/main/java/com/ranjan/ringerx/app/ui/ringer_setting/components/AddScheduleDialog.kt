package com.ranjan.ringerx.app.ui.ringer_setting.components

import android.media.AudioManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onSave: (hour: Int, minute: Int, mode: Int) -> Unit
) {
    val cal = Calendar.getInstance()
    // 1. Remember the time state
    val timeState = rememberTimePickerState(
        initialHour = cal.get(Calendar.HOUR_OF_DAY),
        initialMinute = cal.get(Calendar.MINUTE) + 1,
        is24Hour = false
    )

    // 2. Remember the selected ringer mode
    var selectedMode by remember {
        mutableIntStateOf(AudioManager.RINGER_MODE_NORMAL)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Schedule") },

        // 3. The content is a Column with the TimePicker and Radio buttons
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // The Time Picker
                TimePicker(state = timeState)

                Spacer(modifier = Modifier.height(24.dp))

                // The Radio Button Group
                ModeRadioButtonGroup(
                    selectedMode = selectedMode,
                    onModeSelected = { selectedMode = it }
                )
            }
        },

        // 4. The Save and Cancel buttons
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Pass all the data back on save
                onSave(timeState.hour, timeState.minute, selectedMode)
            }) {
                Text("Save")
            }
        }
    )
}