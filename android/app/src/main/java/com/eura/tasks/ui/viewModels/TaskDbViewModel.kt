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
import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import com.eura.tasks.ui.UiState
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

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

    private val converter = TypeConverter()

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


                var dueDateTime: Instant?

                if (_state.value.taskTimeHour != null && _state.value.taskTimeMinute != null) {
                    val timeString = "${_state.value.taskTimeHour}:${_state.value.taskTimeMinute}:00"

                    val timestamp = _state.value.taskDate
                    val formattedDate = converter.timestampToDateString(timestamp!!)
                    val dueDateTimeString = "${formattedDate}T${timeString}Z"
                    dueDateTime = Instant.parse(dueDateTimeString)

                } else if (_state.value.taskDate != null) {
                    val timestamp = _state.value.taskDate
                    val formattedDate = converter.timestampToDateString(timestamp!!)
                    dueDateTime = Instant.parse(formattedDate)

                } else {
                    dueDateTime = null
                }


                val currentDateTime: Instant = Clock.System.now()

                if (title.isBlank() || parentList.isBlank()) {
                    return
                }

                val task = TaskEntity(
                    title = title,
                    description = description,
                    isFavorite = favorite,
                    isCompleted = false,
                    hasTags = state.value.tagIds.isNotEmpty(),
                    dueDateTime = dueDateTime,
                    taskList = parentList,
                    creationTime = currentDateTime,
                )

                if (_state.value.tagIds.isEmpty() && _state.value.tagUuids.isEmpty()) {
                    viewModelScope.launch {
                        taskDao.upsertTask(task)
                        cleanUpOldLogs { cutoff -> taskDao.deleteLogsOlderThan(cutoff) }
                    }
                } else {
                    viewModelScope.launch {
                        val generatedTaskId = taskDao.upsertTask(task).toInt()

                        Log.d("Values", "${_state.value.tagIds.size} ${_state.value.tagUuids.size}")
                        if (_state.value.tagIds.size == _state.value.tagUuids.size) {
                            for ((tagId, tagUuid) in _state.value.tagIds.zip(_state.value.tagUuids)) {
                                Log.d("Values", "$tagId $tagUuid")
                                tagDao.insertTaskTag(
                                    TaskTagsEntity(
                                        generatedTaskId,
                                        task.uuid,
                                        tagId,
                                        tagUuid
                                    )
                                )
                            }
                        }
                        cleanUpOldLogs { cutoff -> taskDao.deleteLogsOlderThan(cutoff) }
                    }
                }

                Log.d("Test", "Saved")
                _state.update {
                    it.copy(
                        todoTitle = "",
                        todoDescription = "",
                        todoIsFavorite = false,

                        taskDate = null,
                        taskTimeHour = null,
                        taskTimeMinute = null,
                    )
                }
                _uiState.update {
                    it.copy(
                        isAddingTask = false
                    )
                }
            }

            is TaskDbEvent.SetDate -> {
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
                        tagIds = event.tagsId,
                        tagUuids = event.tagsUuid
                    )
                }
            }

            is TaskDbEvent.GetTaskById -> {
                viewModelScope.launch {
                    val taskIds = tagDao.getTasksByTagId(event.id)
                    val tasks = taskDao.getTasksByIds(taskIds)
                    _state.update {
                        it.copy(
                            tasksFromCurrentTag = tasks
                        )
                    }
                }
            }

            is TaskDbEvent.GetAllTasksByTagId -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            taskTags = taskDao.getAllTasksFromTagById(event.tagId)
                        )
                    }
                }
            }

            is TaskDbEvent.RemoveFromTagByTaskId -> {
                viewModelScope.launch {
                    taskDao.removeByTaskId(event.taskId)
                    _state.update { it ->
                        it.copy(
                            taskTags = it.taskTags.filter { it.taskId != event.taskId }
                        )
                    }
                }
            }

            is TaskDbEvent.InsertNewTaskTag -> {
                viewModelScope.launch {
                    tagDao.insertTaskTag(
                        TaskTagsEntity(
                            taskId = event.taskId,
                            taskUuid = event.taskUuid,
                            tagId = event.tagId,
                            tagUuid = event.tagUuid
                        )
                    )
                    _state.update {
                        it.copy(
                            taskTags = it.taskTags + TaskTagsEntity(
                                taskId = event.taskId,
                                taskUuid = event.taskUuid,
                                tagId = event.tagId,
                                tagUuid = event.tagUuid
                            )
                        )
                    }

                }
            }

            is TaskDbEvent.SetTaskDate -> {
                _state.update {
                    it.copy(
                        taskDate = event.date
                    )
                }
            }
            is TaskDbEvent.SetTaskTime -> {
                _state.update {
                    it.copy(
                        taskTimeHour = event.hour,
                        taskTimeMinute = event.minute
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
