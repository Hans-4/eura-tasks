package me.hannes.eura_todo.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.UiState

class UiViewModel: ViewModel() {

    private val _state = MutableStateFlow(UiState())

    val state = _state

    fun onEvent(event: UiEvent) {
        when(event) {
            UiEvent.OpenSortItemSheet -> {
                _state.update {
                    it.copy(
                        isChangingSortType = true
                    )
                }
            }
            UiEvent.CloseSortItemSheet -> {
                _state.update {
                    it.copy(
                        isChangingSortType = false
                    )
                }
            }
            UiEvent.CloseAddTaskListDialog -> {
                _state.update {
                    it.copy(
                        isAddingNewTaskList = false
                    )
                }
            }
            UiEvent.OpenAddTaskListDialog -> {
                _state.update {
                    it.copy(
                        isAddingNewTaskList = true
                    )
                }
            }
            UiEvent.CloseSelectTaskListSheet -> {
                _state.update {
                    it.copy(
                        isSelectingTaskList = false,
                        isAddingTask = true
                    )
                }

            }
            UiEvent.OpenSelectTaskListSheet -> {
                _state.update {
                    it.copy(
                        isSelectingTaskList = true,
                        isAddingTask = false
                    )
                }
            }
            UiEvent.CloseAddTaskSheet -> {
                _state.update {
                    it.copy(
                        isAddingTask = false
                    )
                }
            }
            UiEvent.OpenAddTaskSheet -> {
                _state.update {
                    it.copy(
                        isAddingTask = true
                    )
                }
            }

            UiEvent.CloseAddTaskDescriptionTextField -> {
                _state.update {
                    it.copy(
                        isAddingDescription = false
                    )
                }
            }
            UiEvent.OpenAddTaskDescriptionTextField -> {
                _state.update {
                    it.copy(
                        isAddingDescription = true
                    )
                }
            }

            UiEvent.CloseConfirmDeletionDialog -> {
                _state.update {
                    it.copy(
                        isConfirmingDeletion = false
                    )
                }
            }
            UiEvent.OpenConfirmDeletionDialog -> {
                _state.update {
                    it.copy(
                        isConfirmingDeletion = true
                    )
                }
            }
        }
    }
}