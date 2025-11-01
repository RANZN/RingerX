package com.ranjan.ringerx.app.ui.ringer_setting.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RingerItemUI(
    time: String,
    mode: String,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        },
        positionalThreshold = { distance: Float ->
            distance * 2f
        }
    )

    val progress = dismissState.progress

    val bgColor by animateColorAsState(
        targetValue = Color.Red.copy(alpha = 0.4f + (progress * 0.6f)),
        label = "SwipeBgColor"
    )

    val iconScale by animateFloatAsState(
        targetValue = 0.7f + (progress * 0.5f),
        label = "IconScale"
    )

    val iconAlpha by animateFloatAsState(
        targetValue = 0.3f + (progress * 0.7f),
        label = "IconAlpha"
    )

    val bgShape = RoundedCornerShape(14.dp)
    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(bgShape)
                    .background(bgColor),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = iconAlpha),
                    modifier = Modifier
                        .padding(end = 22.dp)
                        .size((28.dp * iconScale).coerceAtMost(40.dp))
                )
            }
        }, content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = bgShape
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(time, style = MaterialTheme.typography.titleMedium)
                        Text(mode, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    )
}