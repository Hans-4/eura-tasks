package me.hannes.eura_todo.db

sealed interface DbEvent {
    object SaveTask: DbEvent
    object OpenSheet: DbEvent
    object CloseSheet: DbEvent
    data class SetTodoTitle(val title: String): DbEvent
    data class SetTodoDescription(val description: String): DbEvent
    data class SetTodoIsFavorite(val isFavorite: Boolean): DbEvent
    data class SetIsCompleted(val isCompleted: Boolean): DbEvent
    data class SetDate(val date: String): DbEvent
    data class SetTime(val time: String): DbEvent
    data class SortTodos(val sortType: SortType): DbEvent
    data class DeleteTodo(val deleteTodo: TodoEntity): DbEvent
}