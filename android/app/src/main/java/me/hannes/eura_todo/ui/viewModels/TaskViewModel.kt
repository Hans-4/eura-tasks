package me.hannes.eura_todo.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.hannes.eura_todo.db.SortType
import me.hannes.eura_todo.db.TodoDao
import me.hannes.eura_todo.db.TodoEvent
import me.hannes.eura_todo.db.TaskState
import me.hannes.eura_todo.db.TodoEntity

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(
    private val dao: TodoDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.TITLE)
    private val _tasks = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.ID -> dao.getAllTasksByIdAsc()
                SortType.TITLE -> dao.getAllTodosByTitleAsc()
            SortType.DATE -> dao.getAllTasksByDateAsc()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(TaskState())
    val state = combine(_state, _sortType, _tasks) { state, sortType, tasks ->
        state.copy(
            tasks = tasks,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskState())

    fun onEvent(event: TodoEvent) {
        when(event) {
            TodoEvent.SaveTask -> {
                val title = state.value.todoTitle
                val description = state.value.todoDescription

                if (title.isBlank() || description.isBlank()) {
                    return
                }

                val task = TodoEntity(
                    title = title,
                    description = description,
                    isFavorite = false,
                    isCompleted = false,
                    date = "21.07.2026",
                    time = "19:30"
                )
                viewModelScope.launch {
                    dao.upsertTask(task)
                }
                _state.update { it.copy(
                    todoTitle = "",
                    todoDescription = "",
                ) }
            }
            is TodoEvent.SetDate -> {
                _state.update {
                    it.copy(
                        todoDate = event.date
                    )
                }
            }
            is TodoEvent.SetIsCompleted -> {
                _state.update {
                    it.copy(
                        todoIsCompleted = event.isCompleted
                    )
                }
            }
            is TodoEvent.SetTime -> {
                _state.update {
                    it.copy(
                        todoTime = event.time
                    )
                }
            }
            is TodoEvent.SetTodoDescription -> {
                _state.update {
                    it.copy(
                        todoDescription = event.description
                    )
                }
            }
            is TodoEvent.SetTodoIsFavorite -> {
                _state.update {
                    it.copy(
                        todoIsFavorite = event.isFavorite
                    )
                }
            }
            is TodoEvent.SetTodoTitle -> {
                _state.update {
                    it.copy(
                        todoTitle = event.title
                    )
                }
            }
            is TodoEvent.SortTodos -> {
                _sortType.value = event.sortType
            }
            is TodoEvent.DeleteTodo -> {
                viewModelScope.launch {
                    dao.deleteTodo(event.deleteTodo)
                }
            }
        }
    }
}