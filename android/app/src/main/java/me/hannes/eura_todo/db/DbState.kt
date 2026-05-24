package me.hannes.eura_todo.db

data class DbState(
    val tasks: List<TodoEntity> = emptyList(),
    val todoTitle: String = "",
    val todoDescription: String = "",
    val todoIsFavorite: Boolean = false,
    val todoIsCompleted: Boolean = false,
    val todoDate: String = "",
    val todoTime: String = "",
    val taskParentList: String = "",
    val sortType: SortType = SortType.TITLE,
)
