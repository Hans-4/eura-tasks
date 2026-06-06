package me.hannes.eura_tasks.ui.viewModels

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
import me.hannes.eura_tasks.db.SortType
import me.hannes.eura_tasks.db.TaskDbDao
import me.hannes.eura_tasks.db.DbEvent
import me.hannes.eura_tasks.db.TaskDbState
import me.hannes.eura_tasks.db.DeletedTasksEntity
import me.hannes.eura_tasks.db.TodoEntity
import me.hannes.eura_tasks.db.dbFunctions.cleanUpOldData
import me.hannes.eura_tasks.ui.UiState
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class DbViewModel(
    private val dao: TaskDbDao
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

    private val _uiState = MutableStateFlow(UiState())
    private val _state = MutableStateFlow(TaskDbState())
    val state = combine(_state, _sortType, _tasks) { state, sortType, tasks ->
        state.copy(
            tasks = tasks,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskDbState())

    fun onEvent(event: DbEvent) {
        when(event) {
            DbEvent.SaveTask -> {
                val title = state.value.todoTitle
                val description = state.value.todoDescription
                val favorite = state.value.todoIsFavorite
                val parentList = state.value.taskParentList

                val currentDateTime: Instant = Clock.System.now()

                if (title.isBlank() || parentList.isBlank()) {
                    return
                }

                val task = TodoEntity(
                    title = title,
                    description = description,
                    isFavorite = favorite,
                    isCompleted = false,
                    date = "21.07.2026",
                    time = "19:30",
                    taskList = parentList,
                    creationTime = currentDateTime
                )
                viewModelScope.launch {
                    dao.upsertTask(task)
                    cleanUpOldData(dao)
                }
                _state.update { it.copy(
                    todoTitle = "",
                    todoDescription = "",
                    todoIsFavorite = false,
                ) }
                _uiState.update {
                    it.copy(
                        isAddingTask = false
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
            is DbEvent.SelectTaskList -> {
                _state.update {
                    it.copy(
                        taskParentList = event.listType
                    )
                }
                _uiState.update {
                    it.copy(
                        isSelectingTaskList = false
                    )
                }
            }
            is DbEvent.DeleteTodo -> {
                viewModelScope.launch {
                    dao.deleteTodo(event.deleteTodo)
                }
            }
            is DbEvent.DeleteTodoById -> {
                val currentDateTime: Instant = Clock.System.now()

                viewModelScope.launch {
                    val deletedTask = DeletedTasksEntity(
                        deletedUuid = dao.getTaskUuid(event.id),
                        deletionDate = currentDateTime
                    )
                    dao.upsertDeletedTask(deletedTask)
                    dao.deleteTodoById(event.id)
                }
            }
            is DbEvent.SetParentList -> {
                _state.update {
                    it.copy(
                        taskParentList = event.parentList
                    )
                }
            }
        }
    }

    suspend fun exists(uuid: String): Boolean {
        return dao.exists(uuid)
    }

    suspend fun deleted(uuid: String): Boolean {
        return dao.deleted(uuid)
    }

    /**
     * Helper for the Cloud Sync: Insert a task downloaded from Drive
     */
    fun insertTask(task: TodoEntity) {
        viewModelScope.launch {
            dao.upsertTask(task)
        }
    }
}