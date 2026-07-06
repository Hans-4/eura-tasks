package com.eura.tasks.db.tasks

import java.time.LocalDateTime

sealed interface TaskDbEvent {
    object SaveTask: TaskDbEvent
    data class SetTodoTitle(val title: String): TaskDbEvent
    data class SetTodoDescription(val description: String): TaskDbEvent
    data class SetTodoIsFavorite(val isFavorite: Boolean, val task: TaskEntity? = null): TaskDbEvent
    data class SetIsCompleted(val isCompleted: Boolean, val task: TaskEntity? = null): TaskDbEvent
    data class SetDate(val date: LocalDateTime): TaskDbEvent
    data class SetTaskTags(val tagsUuid: List<String>, val tagsId: List<Int>): TaskDbEvent

    data class SetParentList(val parentList: String): TaskDbEvent
    data class SortTodos(val sortType: SortType): TaskDbEvent
    data class SelectTaskList(val listType: String): TaskDbEvent
    data class DeleteTodoById(val id: Int): TaskDbEvent
    data class SetSearchQuery(val query: String): TaskDbEvent
    data class UpdateTaskTitleById(val id: Int, val newTitle: String): TaskDbEvent
    data class UpdateDescriptionById(val id: Int, val newDescription: String): TaskDbEvent
    data class GetTaskById(val id: Int): TaskDbEvent
    data class GetAllTasksByTagId(val tagId: Int): TaskDbEvent
    data class InsertNewTaskTag(val taskId: Int, val taskUuid: String, val tagId: Int, val tagUuid: String): TaskDbEvent
    data class RemoveFromTagByTaskId(val taskId: Int): TaskDbEvent

    data class SetTaskDate(val date: Long?): TaskDbEvent
    data class SetTaskTime(val hour: Int, val minute: Int): TaskDbEvent
}