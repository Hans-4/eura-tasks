package me.hannes.eura_tasks.db.tasks

sealed interface TaskDbEvent {
    object SaveTask: TaskDbEvent
    data class SetTodoTitle(val title: String): TaskDbEvent
    data class SetTodoDescription(val description: String): TaskDbEvent
    data class SetTodoIsFavorite(val isFavorite: Boolean, val task: TaskEntity? = null): TaskDbEvent
    data class SetIsCompleted(val isCompleted: Boolean, val task: TaskEntity? = null): TaskDbEvent
    data class SetDate(val date: String): TaskDbEvent
    data class SetTime(val time: String): TaskDbEvent
    data class SetParentList(val parentList: String): TaskDbEvent
    data class SortTodos(val sortType: SortType): TaskDbEvent
    data class SelectTaskList(val listType: String): TaskDbEvent
    data class DeleteTodoById(val id: Int): TaskDbEvent
    data class SetSearchQuery(val query: String): TaskDbEvent
    data class UpdateTaskTitleById(val id: Int, val newTitle: String): TaskDbEvent
    data class UpdateDescriptionById(val id: Int, val newDescription: String): TaskDbEvent
}