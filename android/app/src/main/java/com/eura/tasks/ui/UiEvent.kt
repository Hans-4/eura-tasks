package com.eura.tasks.ui

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
    object OpenItemWithSimilarNameWarningDialog: UiEvent
    object CloseItemWithSimilarNameWarningDialog: UiEvent
    data class SetReason(val reason: Int): UiEvent
    object OpenDeleteAllTasksWarningDialog: UiEvent
    object CloseDeleteAllTasksWarningDialog: UiEvent
    object OpenListConflictWarningAlertOpen: UiEvent
    object CloseListConflictWarningAlertOpen: UiEvent
    object OpenDeleteAllCloudDataWarningDialog: UiEvent
    object CloseDeleteAllCloudDataWarningDialog: UiEvent

    object OpenAddTagsDialog: UiEvent
    object CloseAddTagsDialog: UiEvent

    object OpenAddTagTextField: UiEvent
    object CloseAddTagTextField: UiEvent

    object OpenSetSearchFilterBottomSheet: UiEvent
    object CloseSetSearchFilterBottomSheet: UiEvent

    object OpenManageTagSheet: UiEvent
    object CloseManageTagSheet: UiEvent

    object OpenAddReminderDialog: UiEvent
    object CloseAddReminderDialog: UiEvent

    object OpenTimePickDialog: UiEvent
    object CloseTimePickDialog: UiEvent

    object OpenAddRepeatsDialog: UiEvent
    object CloseAddRepeatsDialog: UiEvent

    data class OpenDatePickDialog(val openedFrom: Int): UiEvent //1 = Start date, 2 = End date
    object CloseDatePickDialog: UiEvent

    object OpenNotificationPermissionScreen: UiEvent
    object CloseNotificationPermissionScreen: UiEvent

    data class SetNotificationPermissionState(val state: Boolean): UiEvent
    data class SetAlarmPermissionState(val state: Boolean): UiEvent
}