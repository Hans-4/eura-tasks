package com.eura.tasks.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eura.tasks.db.cleanUpOldLogs
import com.eura.tasks.db.tags.TagDbDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.eura.tasks.db.tasks.SortType
import com.eura.tasks.db.tasks.TaskDbDao
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.db.tasks.DeletedTasksEntity
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.ui.UiState
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDbViewModel(
    private val taskDao: TaskDbDao,
    private val tagDao: TagDbDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.ID)
    private val _tasks = _sortType //TODO: Implement toggle to toggle Asc and Desc
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.ID -> taskDao.getAllTasksByIdDesc()
                SortType.TITLE -> taskDao.getAllTodosByTitleAsc()
            SortType.DATE -> taskDao.getAllTasksByDateAsc()
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

                Log.d("Check", "Selected tags ${state.value.tags}")

                val currentDateTime: Instant = Clock.System.now()

                if (title.isBlank() || parentList.isBlank()) {
                    return
                }

                val task = TaskEntity(
                    title = title,
                    description = description,
                    isFavorite = favorite,
                    isCompleted = false,
                    dueDateTime = null,
                    taskList = parentList,
                    creationTime = currentDateTime,
                )

                if (_state.value.tags.isEmpty()) {
                    viewModelScope.launch {
                        taskDao.upsertTask(task)
                        cleanUpOldLogs { cutoff -> taskDao.deleteLogsOlderThan(cutoff) }
                    }
                } else {
                    viewModelScope.launch {
                        taskDao.upsertTask(task)
                        val entryUuid = task.uuid

                        for (tag in _state.value.tags) {
                            tagDao.insertTaskTags(entryUuid, tag)
                        }

                        cleanUpOldLogs { cutoff -> taskDao.deleteLogsOlderThan(cutoff) }
                    }
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

            is TaskDbEvent.SetDueDateTime -> {
                _state.update {
                    it.copy(
                        dueDateTime = event.date
                    )
                }
            }
            is TaskDbEvent.SetIsCompleted -> {
                if (event.task != null) {
                    viewModelScope.launch {
                        taskDao.upsertTask(event.task.copy(isCompleted = event.isCompleted))
                    }
                } else {
                    _state.update {
                        it.copy(
                            todoIsCompleted = event.isCompleted
                        )
                    }
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
                if (event.task != null) {
                    viewModelScope.launch {
                        taskDao.upsertTask(event.task.copy(isFavorite = event.isFavorite))
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
                        deletedUuid = taskDao.getTaskUuid(event.id),
                        deletionDate = currentDateTime
                    )
                    taskDao.upsertDeletedTask(deletedTask)
                    taskDao.deleteTodoById(event.id)
                }
            }
            is TaskDbEvent.SetParentList -> {
                _state.update {
                    it.copy(
                        taskParentList = event.parentList
                    )
                }
            }

            is TaskDbEvent.SetSearchQuery -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query
                    )
                }
                viewModelScope.launch {
                    val results = taskDao.searchForTasks(event.query)
                    _state.update {
                        it.copy(
                            searchResults = results
                        )
                    }
                }
            }

            is TaskDbEvent.UpdateTaskTitleById -> {
                viewModelScope.launch {
                    taskDao.updateTaskTitle(event.id, event.newTitle)
                }
            }

            is TaskDbEvent.UpdateDescriptionById -> {
                viewModelScope.launch {
                    taskDao.updateTaskDescription(event.id, event.newDescription)
                }
            }

            is TaskDbEvent.SetTaskTags -> {
                _state.update {
                    it.copy(
                        tags = event.tags
                    )
                }
            }
        }
    }

    /**
     * Helper for the Cloud Sync: Insert a task downloaded from Drive
     */
    fun insertTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.upsertTask(task)
        }
    }
}