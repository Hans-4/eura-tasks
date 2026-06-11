package me.hannes.eura_tasks.db.tasks

data class TaskDbState(
    val tasks: List<TaskEntity> = emptyList(),
    val todoTitle: String = "",
    val todoDescription: String = "",
    val todoIsFavorite: Boolean = false,
    val todoIsCompleted: Boolean = false,
    val todoDate: String = "",
    val todoTime: String = "",
    val taskParentList: String = "",
    val sortType: SortType = SortType.TITLE,

    val searchQuery: String = "",
    val searchResults: List<TaskEntity> = emptyList()
)
