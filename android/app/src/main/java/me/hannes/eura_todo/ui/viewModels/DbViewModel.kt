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
import me.hannes.eura_todo.db.DbDao
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.db.TodoEntity

@OptIn(ExperimentalCoroutinesApi::class)
class DbViewModel(
    private val dao: DbDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.ID)
    private val _tasks = _sortType //TODO: Implement toggle to toggle Asc and Desc
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.ID -> dao.getAllTasksByIdDesc()
                SortType.TITLE -> dao.getAllTodosByTitleAsc()
            SortType.DATE -> dao.getAllTasksByDateAsc()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(DbState())
    val state = combine(_state, _sortType, _tasks) { state, sortType, tasks ->
        state.copy(
            tasks = tasks,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DbState())

    fun onEvent(event: DbEvent) {
        when(event) {
            DbEvent.SaveTask -> {
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
                    isAddingTask = false,
                    todoTitle = "",
                    todoDescription = "",
                ) }
            }
            DbEvent.CloseSheet -> {
                _state.update { it.copy(
                    isAddingTask = false
                ) }
            }
            DbEvent.OpenSheet -> {
                _state.update {
                    it.copy(
                        isAddingTask = true
                    )
                }
            }

            is DbEvent.SetDate -> {
                _state.update {
                    it.copy(
                        todoDate = event.date
                    )
                }
            }
            is DbEvent.SetIsCompleted -> {
                if (event.todo != null) {
                    viewModelScope.launch {
                        dao.upsertTask(event.todo.copy(isCompleted = event.isCompleted))
                    }
                } else {
                    _state.update {
                        it.copy(
                            todoIsCompleted = event.isCompleted
                        )
                    }
                }
            }
            is DbEvent.SetTime -> {
                _state.update {
                    it.copy(
                        todoTime = event.time
                    )
                }
            }
            is DbEvent.SetTodoDescription -> {
                _state.update {
                    it.copy(
                        todoDescription = event.description
                    )
                }
            }
            is DbEvent.SetTodoIsFavorite -> {
                if (event.todo != null) {
                    viewModelScope.launch {
                        dao.upsertTask(event.todo.copy(isFavorite = event.isFavorite))
                    }
                } else {
                    _state.update {
                        it.copy(
                            todoIsFavorite = event.isFavorite
                        )
                    }
                }
            }
            is DbEvent.SetTodoTitle -> {
                _state.update {
                    it.copy(
                        todoTitle = event.title
                    )
                }
            }
            is DbEvent.SortTodos -> {
                _sortType.value = event.sortType
            }
            is DbEvent.DeleteTodo -> {
                viewModelScope.launch {
                    dao.deleteTodo(event.deleteTodo)
                }
            }
        }
    }
}