package com.eura.tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState

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

            UiEvent.CloseHomeFABMenu -> {
                _state.update {
                    it.copy(
                        isHomeFABMenuExpanded = false
                    )
                }
            }
            UiEvent.OpenHomeFABMenu -> {
                _state.update {
                    it.copy(
                        isHomeFABMenuExpanded = true
                    )
                }
            }

            UiEvent.CloseManageListSheet -> {
                _state.update {
                    it.copy(
                        isManageListSheetOpen = false
                    )
                }
            }
            UiEvent.OpenManageListSheet -> {
                _state.update {
                    it.copy(
                        isManageListSheetOpen = true
                    )
                }
            }

            UiEvent.CloseItemWithSimilarNameWarningDialog -> {
                _state.update {
                    it.copy(
                        isItemWithSimilarNameWarningDialogOpen = false
                    )
                }
            }
            UiEvent.OpenItemWithSimilarNameWarningDialog -> {
                _state.update {
                    it.copy(
                        isItemWithSimilarNameWarningDialogOpen = true
                    )
                }
            }
            is UiEvent.SetReason -> {
                _state.update {
                    it.copy(
                        similarNameWarningReason = event.reason
                    )
                }
            }

            UiEvent.CloseDeleteAllTasksWarningDialog -> {
                _state.update {
                    it.copy(
                        isDeleteAllTasksWarningDialogOpen = false
                    )
                }
            }
            UiEvent.OpenDeleteAllTasksWarningDialog -> {
                _state.update {
                    it.copy(
                        isDeleteAllTasksWarningDialogOpen = true
                    )
                }
            }

            UiEvent.CloseListConflictWarningAlertOpen -> {
                _state.update {
                    it.copy(
                        isListConflictWarningAlertOpen = false
                    )
                }
            }
            UiEvent.OpenListConflictWarningAlertOpen -> {
                _state.update {
                    it.copy(
                        isListConflictWarningAlertOpen = true
                    )
                }
            }

            UiEvent.CloseDeleteAllCloudDataWarningDialog -> {
                _state.update {
                    it.copy(
                        isDeleteAllCloudDataWarningDialogOpen = false
                    )
                }
            }
            UiEvent.OpenDeleteAllCloudDataWarningDialog -> {
                _state.update {
                    it.copy(
                        isDeleteAllCloudDataWarningDialogOpen = true
                    )
                }
            }

            UiEvent.CloseAddTagsDialog -> {
                _state.update {
                    it.copy(
                        isAddTagsDialogOpen = false
                    )
                }
            }
            UiEvent.OpenAddTagsDialog -> {
                _state.update {
                    it.copy(
                        isAddTagsDialogOpen = true
                    )
                }
            }

            UiEvent.CloseAddTagTextField -> {
                _state.update {
                    it.copy(
                        isAddTagTextFieldOpen = false
                    )
                }
            }
            UiEvent.OpenAddTagTextField -> {
                _state.update {
                    it.copy(
                        isAddTagTextFieldOpen = true
                    )
                }
            }

            UiEvent.CloseSetSearchFilterBottomSheet -> {
                _state.update {
                    it.copy(
                        isSetSearchFilterBottomSheetOpen = false
                    )
                }
            }
            UiEvent.OpenSetSearchFilterBottomSheet -> {
                _state.update {
                    it.copy(
                        isSetSearchFilterBottomSheetOpen = true
                    )
                }
            }

            UiEvent.CloseManageTagSheet -> {
                _state.update {
                    it.copy(
                        isManageTagSheetOpen = false
                    )
                }
            }
            UiEvent.OpenManageTagSheet -> {
                _state.update {
                    it.copy(
                        isManageTagSheetOpen = true
                    )
                }
            }

            UiEvent.CloseAddReminderDialog -> {
                _state.update {
                    it.copy(
                        isAddReminderDialogOpen = false
                    )
                }
            }
            UiEvent.OpenAddReminderDialog -> {
                _state.update {
                    it.copy(
                        isAddReminderDialogOpen = true
                    )
                }
            }

            UiEvent.CloseTimePickDialog -> {
                _state.update {
                    it.copy(
                        isPickingTime = false
                    )
                }
            }
            UiEvent.OpenTimePickDialog -> {
                _state.update {
                    it.copy(
                        isPickingTime = true
                    )
                }
            }

            UiEvent.CloseAddRepeatsDialog -> {
                _state.update {
                    it.copy(
                        isAddingRepeats = false,
                        isAddingTask = true
                    )
                }
            }
            UiEvent.OpenAddRepeatsDialog -> {
                _state.update {
                    it.copy(
                        isAddingTask = false,
                        isAddingRepeats = true
                    )
                }
            }

            UiEvent.CloseDatePickDialog -> {
                _state.update {
                    it.copy(
                        isDatePickDialogOpen = false
                    )
                }
            }
            is UiEvent.OpenDatePickDialog -> {
                _state.update {
                    it.copy(
                        isDatePickDialogOpen = true,
                        datePickDialogOpenedFrom = event.openedFrom
                    )
                }
            }
        }
    }
}