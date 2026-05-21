package me.hannes.eura_todo.db

sealed interface TodoEvent {
    object SaveTask: TodoEvent
    object OpenSheet: TodoEvent
    object CloseSheet: TodoEvent
    data class SetTodoTitle(val title: String): TodoEvent
    data class SetTodoDescription(val description: String): TodoEvent
    data class SetTodoIsFavorite(val isFavorite: Boolean): TodoEvent
    data class SetIsCompleted(val isCompleted: Boolean): TodoEvent
    data class SetDate(val date: String): TodoEvent
    data class SetTime(val time: String): TodoEvent
    data class SortTodos(val sortType: SortType): TodoEvent
    data class DeleteTodo(val deleteTodo: TodoEntity): TodoEvent
}