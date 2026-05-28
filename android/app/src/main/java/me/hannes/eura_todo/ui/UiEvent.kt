package me.hannes.eura_todo.ui

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
}