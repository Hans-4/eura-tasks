package com.eura.tasks.db.tasks

import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import java.time.LocalDateTime

data class TaskDbState(
    val tasks: List<TaskEntity> = emptyList(),
    val taskTitle: String = "",
    val todoDescription: String = "",
    val todoIsFavorite: Boolean = false,
    val todoIsCompleted: Boolean = false,
    val taskHasTags: Boolean = false,
    val dueDateTime: LocalDateTime? = null,
    val taskParentList: String = "",
    val taskParentListId: String = "",
    val tagUuids: List<String> = emptyList(),
    val sortType: SortType = SortType.TITLE,
    val tasksFromCurrentTag: List<TaskEntity> = emptyList(),

    val searchQuery: String = "",
    val searchResults: List<TaskEntity> = emptyList(),

    val taskTags: List<TaskTagsEntity> = emptyList(),

    val taskDate: Long? = null,
    val taskTimeHour: Int? = null,
    val taskTimeMinute: Int? = null,
)
