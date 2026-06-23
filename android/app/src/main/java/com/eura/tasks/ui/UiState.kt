package com.eura.tasks.ui

data class UiState(
    val isChangingSortType: Boolean = false,
    val isAddingNewTaskList: Boolean = false,
    val isSelectingTaskList: Boolean = false,
    val isAddingTask: Boolean = false,
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
    val isAddTagTextFieldOpen: Boolean = false
)