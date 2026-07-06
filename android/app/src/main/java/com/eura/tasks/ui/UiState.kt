package com.eura.tasks.ui

data class UiState(
    val isChangingSortType: Boolean = false,
    val isAddingNewTaskList: Boolean = false,
    val isSelectingTaskList: Boolean = false,
    val isAddingTask: Boolean = false,
    val isAddingRepeats: Boolean = false,
    val isAddingDescription: Boolean = false,
    val isConfirmingDeletion: Boolean = false,
    val isHomeFABMenuExpanded: Boolean = false,
    val isManageListSheetOpen: Boolean = false,

    val isItemWithSimilarNameWarningDialogOpen: Boolean = false,
    val similarNameWarningReason: Int = 0,

    val isDeleteAllTasksWarningDialogOpen: Boolean = false,
    val isListConflictWarningAlertOpen: Boolean = false,
    val isDeleteAllCloudDataWarningDialogOpen: Boolean = false,
    val isAddTagsDialogOpen: Boolean = false,
    val isAddTagTextFieldOpen: Boolean = false,

    val isSetSearchFilterBottomSheetOpen: Boolean = false,

    val isManageTagSheetOpen: Boolean = false,

    val isAddReminderDialogOpen: Boolean = false,

    val isPickingTime: Boolean = false,

    val isDatePickDialogOpen: Boolean = false,
    val datePickDialogOpenedFrom: Int = 1, //1 = Start date, 2 = End date

    val isNotificationPermissionScreenOpen: Boolean = false,
)