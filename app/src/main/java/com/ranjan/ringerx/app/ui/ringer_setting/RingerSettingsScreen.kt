package com.ranjan.ringerx.app.ui.ringer_setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RingerSettingsScreen(
    uiState: RingerSettingsViewModel.UiState,
    modifier: Modifier = Modifier,
    onAction: (RingerSettingsViewModel.Action) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        items(uiState.events, key = { it.id }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(it.getTimeString())
                Text(it.getModeName())
            }
        }
    }
}