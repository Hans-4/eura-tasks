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
import me.hannes.eura_tasks.db.tasks.SortType
import me.hannes.eura_tasks.db.tasks.TaskDbDao
import me.hannes.eura_tasks.db.tasks.TaskDbEvent
import me.hannes.eura_tasks.db.tasks.TaskDbState
import me.hannes.eura_tasks.db.tasks.DeletedTasksEntity
import me.hannes.eura_tasks.db.tasks.TodoEntity
import me.hannes.eura_tasks.db.cleanUpOldTasks
import me.hannes.eura_tasks.ui.UiState
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDbViewModel(
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

    fun onEvent(event: TaskDbEvent) {
        when(event) {
            TaskDbEvent.SaveTask -> {
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
                    cleanUpOldTasks(dao)
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

            is TaskDbEvent.SetDate -> {
                _state.update {
                    it.copy(
                        todoDate = event.date
                    )
                }
            }
            is TaskDbEvent.SetIsCompleted -> {
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
            is TaskDbEvent.SetTime -> {
                _state.update {
                    it.copy(
                        todoTime = event.time
                    )
                }
            }
            is TaskDbEvent.SetTodoDescription -> {
                _state.update {
                    it.copy(
                        todoDescription = event.description
                    )
                }
            }
            is TaskDbEvent.SetTodoIsFavorite -> {
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
            is TaskDbEvent.SetTodoTitle -> {
                _state.update {
                    it.copy(
                        todoTitle = event.title
                    )
                }
            }
            is TaskDbEvent.SortTodos -> {
                _sortType.value = event.sortType
            }
            is TaskDbEvent.SelectTaskList -> {
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
            is TaskDbEvent.DeleteTodoById -> {
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
            is TaskDbEvent.SetParentList -> {
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