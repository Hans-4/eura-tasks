package com.eura.tasks.db.tasks

import java.time.LocalDateTime

data class TaskDbState(
    val tasks: List<TaskEntity> = emptyList(),
    val todoTitle: String = "",
    val todoDescription: String = "",
    val todoIsFavorite: Boolean = false,
    val todoIsCompleted: Boolean = false,
    val taskHasTags: Boolean = false,
    val dueDateTime: LocalDateTime? = null,
    val taskParentList: String = "",
    val tagIds: List<Int> = emptyList(),
    val tagUuids: List<String> = emptyList(),
    val sortType: SortType = SortType.TITLE,
    val tasksFromCurrentTag: List<TaskEntity> = emptyList(),

    val searchQuery: String = "",
    val searchResults: List<TaskEntity> = emptyList()
)
