package me.hannes.eura_tasks.db

import me.hannes.eura_tasks.db.SortType
import me.hannes.eura_tasks.db.TodoEntity

data class TaskDbState(
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