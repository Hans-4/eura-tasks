package me.hannes.eura_todo.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.UiState

class HomeViewModel: ViewModel() {

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
        }
    }
}