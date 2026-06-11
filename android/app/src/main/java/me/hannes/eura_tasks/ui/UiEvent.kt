package me.hannes.eura_tasks.ui

sealed interface UiEvent {
    object OpenSortItemSheet: UiEvent
    object CloseSortItemSheet: UiEvent
    object OpenAddTaskListDialog: UiEvent
    object CloseAddTaskListDialog: UiEvent
    object OpenSelectTaskListSheet: UiEvent
    object CloseSelectTaskListSheet: UiEvent
    object OpenAddTaskSheet: UiEvent
    object CloseAddTaskSheet: UiEvent
    object OpenAddTaskDescriptionTextField: UiEvent
    object CloseAddTaskDescriptionTextField: UiEvent
    object OpenConfirmDeletionDialog: UiEvent
    object CloseConfirmDeletionDialog: UiEvent
    object OpenHomeFABMenu: UiEvent
    object CloseHomeFABMenu: UiEvent
    object OpenManageListSheet: UiEvent
    object CloseManageListSheet: UiEvent
    object OpenListWithSimilarNameWarningDialog: UiEvent
    object CloseListWithSimilarNameWarningDialog: UiEvent
    object OpenDeleteAllTasksWarningDialog: UiEvent
    object CloseDeleteAllTasksWarningDialog: UiEvent
    object OpenListConflictWarningAlertOpen: UiEvent
    object CloseListConflictWarningAlertOpen: UiEvent
}