package me.hannes.eura_tasks.db

import me.hannes.eura_tasks.db.tasks.SortType
import me.hannes.eura_tasks.db.tasks.TodoEntity

sealed interface DbEvent {
    object SaveTask: DbEvent
    data class SetTodoTitle(val title: String): DbEvent
    data class SetTodoDescription(val description: String): DbEvent
    data class SetTodoIsFavorite(val isFavorite: Boolean, val todo: TodoEntity? = null): DbEvent
    data class SetIsCompleted(val isCompleted: Boolean, val todo: TodoEntity? = null): DbEvent
    data class SetDate(val date: String): DbEvent
    data class SetTime(val time: String): DbEvent
    data class SetParentList(val parentList: String): DbEvent
    data class SortTodos(val sortType: SortType): DbEvent
    data class SelectTaskList(val listType: String): DbEvent
    data class DeleteTodo(val deleteTodo: TodoEntity): DbEvent
    data class DeleteTodoById(val id: Int): DbEvent
}