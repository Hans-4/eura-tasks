package me.hannes.eura_todo.db

data class TaskState(
    val tasks: List<TodoEntity> = emptyList(),
    val todoTitle: String = "",
    val todoDescription: String = "",
    val todoIsFavorite: Boolean = false,
    val todoIsCompleted: Boolean = false,
    val todoDate: String = "",
    val todoTime: String = "",
    val sortType: SortType = SortType.TITLE,
)
