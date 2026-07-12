package com.eura.tasks.db.tasks

import java.time.LocalDateTime

sealed interface TaskDbEvent {
    object SaveTask: TaskDbEvent
    data class SetTaskTitle(val title: String): TaskDbEvent
    data class SetTodoDescription(val description: String): TaskDbEvent
    data class SetTodoIsFavorite(val isFavorite: Boolean, val task: TaskEntity? = null): TaskDbEvent
    data class SetIsCompleted(val isCompleted: Boolean, val task: TaskEntity? = null): TaskDbEvent
    data class SetDate(val date: LocalDateTime): TaskDbEvent
    data class SetTaskTags(val tagsUuid: List<String>): TaskDbEvent

    data class SetParentList(val parentList: String): TaskDbEvent

    data class SortTodos(val sortType: SortType): TaskDbEvent
    data class SelectTaskList(val listType: String): TaskDbEvent
    data class DeleteTaskByUuid(val uuid: String): TaskDbEvent
    data class SetSearchQuery(val query: String): TaskDbEvent
    data class UpdateTaskTitleByUuid(val uuid: String, val newTitle: String): TaskDbEvent
    data class UpdateDescriptionByUuid(val uuid: String, val newDescription: String): TaskDbEvent
    data class GetTaskById(val id: String): TaskDbEvent
    data class GetAllTasksByTagUuid(val uuid: String): TaskDbEvent
    data class InsertNewTaskTag(val taskId: String, val tagId: String, val isActive: Boolean): TaskDbEvent
    data class UpdateTaskTag(val taskId: String, val tagId: String, val isActive: Boolean): TaskDbEvent

    data class SetTaskDate(val date: Long?): TaskDbEvent
    data class SetTaskTime(val hour: Int?, val minute: Int?): TaskDbEvent
    data class UpdateTaskDateTime(val taskUuid: String, val date: Long?, ): TaskDbEvent
}