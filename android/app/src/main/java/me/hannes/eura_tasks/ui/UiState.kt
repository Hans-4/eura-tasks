package me.hannes.eura_tasks.ui

data class UiState(
    val isChangingSortType: Boolean = false,
    val isAddingNewTaskList: Boolean = false,
    val isSelectingTaskList: Boolean = false,
    val isAddingTask: Boolean = false,
    val isAddingDescription: Boolean = false,
    val isConfirmingDeletion: Boolean = false,
    val isHomeFABMenuExpanded: Boolean = false,
    val isManageListSheetOpen: Boolean = false
)
