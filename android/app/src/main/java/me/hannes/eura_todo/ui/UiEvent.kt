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
}