package com.ranjan.ringerx.app.ui.ringer_setting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranjan.ringerx.app.ui.ringer_setting.components.AddScheduleDialog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RingerSettingsHost() {
    val viewModel: RingerSettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Auto-disable Vibrate Mode") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.handleAction(RingerSettingsViewModel.Action.AddScheduleClicked) }) {
                Icon(Icons.Default.Add, contentDescription = "Add new schedule")
            }
        },
    ) { innerPadding ->
        RingerSettingsScreen(
            uiState = uiState,
            modifier = Modifier.padding(innerPadding),
            onAction = viewModel::handleAction
        )
    }

    if (uiState.isAddScheduleDialogVisible) {
        AddScheduleDialog(
            onDismiss = { viewModel.handleAction(RingerSettingsViewModel.Action.DialogDismissed) },
            onSave = { hour, minute, mode ->
                viewModel.handleAction(RingerSettingsViewModel.Action.AddItem(hour, minute, mode))
            }
        )
    }
}