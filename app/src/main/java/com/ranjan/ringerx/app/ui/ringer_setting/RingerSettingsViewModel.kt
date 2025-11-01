package com.ranjan.ringerx.app.ui.ringer_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjan.ringerx.app.data.model.RingerEvent
import com.ranjan.ringerx.app.utils.Prefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RingerSettingsViewModel(
    private val prefs: Prefs
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.onStart {
        loadEvents()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    /**
     * The single entry point for all UI actions (INTENTS).
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.AddScheduleClicked -> {
                _uiState.update { it.copy(isAddScheduleDialogVisible = true) }
            }

            is Action.AddItem -> {
                val newEvent = RingerEvent(
                    id = System.currentTimeMillis(),
                    hour = action.hour,
                    minute = action.minute,
                    mode = action.mode
                )

                val currentEvents = _uiState.value.events
                val updatedEvents = (currentEvents + newEvent)
                    .sortedBy { it.hour * 60 + it.minute }

                saveEvents(updatedEvents)
                _uiState.update {
                    it.copy(
                        events = updatedEvents,
                        isAddScheduleDialogVisible = false
                    )
                }
            }

            is Action.DeleteItem -> {
                val currentEvents = _uiState.value.events
                val updatedEvents = currentEvents - action.event

                saveEvents(updatedEvents)
                _uiState.update { it.copy(events = updatedEvents) }
            }

            is Action.DialogDismissed -> {
                _uiState.update {
                    it.copy(isAddScheduleDialogVisible = false)
                }
            }
        }
    }

    /**
     * Loads the event list from SharedPreferences and updates the state.
     */
    private fun loadEvents() {
        viewModelScope.launch {
            val events = prefs.loadEvents()
            _uiState.update {
                it.copy(events = events.sortedBy { e -> e.hour * 60 + e.minute })
            }
        }
    }

    /**
     * Saves the provided event list to SharedPreferences in the background.
     */
    private fun saveEvents(events: List<RingerEvent>) = viewModelScope.launch {
        prefs.saveEvents(events)
    }

    data class UiState(
        val events: List<RingerEvent> = emptyList(),
        val isAddScheduleDialogVisible: Boolean = false,
    )

    sealed interface Action {
        data object AddScheduleClicked : Action
        data class AddItem(val hour: Int, val minute: Int, val mode: Int) : Action
        data class DeleteItem(val event: RingerEvent) : Action
        data object DialogDismissed : Action
    }
}