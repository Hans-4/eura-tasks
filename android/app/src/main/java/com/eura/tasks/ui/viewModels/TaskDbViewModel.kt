package com.eura.tasks.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eura.tasks.db.cleanUpOldLogs
import com.eura.tasks.db.deletedItems.DeletedItemsDao
import com.eura.tasks.db.deletedItems.DeletedItemsEntity
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
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent.*
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import com.eura.tasks.notifications.AlarmScheduler
import com.eura.tasks.ui.UiState
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDbViewModel(
    private val taskDao: TaskDbDao,
    private val tagDao: TagDbDao,
    private val alarmScheduler: AlarmScheduler,
    private val deletedItemsDao: DeletedItemsDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.ID)
    private val _tasks = _sortType //TODO: Implement toggle to toggle Asc and Desc
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.ID -> taskDao.getAllTasksByUuidDesc()
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

    fun onEvent(
        event: TaskDbEvent,
        onRepeatDbEvent: (RepeatDbEvent) -> Unit,
        repeatDbState: RepeatDbState
    ) {
        when(event) {
            TaskDbEvent.SaveTask -> {
                viewModelScope.launch {
                    val currentState = _state.value
                    val title = currentState.taskTitle
                    val description = currentState.todoDescription
                    val favorite = currentState.todoIsFavorite
                    var parentListId = currentState.taskParentListId

                    if (parentListId.isBlank() && currentState.taskParentList.isNotBlank()) {
                        parentListId = taskDao.getParentListId(currentState.taskParentList) ?: ""
                    }

                    if (title.isBlank() || parentListId.isBlank()) {
                        Log.d("Test", "Title or list is blank $title $parentListId")
                        return@launch
                    }

                    val repeatType =
                        if (repeatDbState.toSave) repeatDbState.selectedRepeatType else null

                    val dueDateTime: Instant? = currentState.taskDate?.let { timestamp ->
                        val date = Instant.fromEpochMilliseconds(timestamp)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date

                        val hour = currentState.taskTimeHour ?: 9
                        val minute = currentState.taskTimeMinute ?: 0

                        LocalDateTime(
                            date.year,
                            date.month,
                            date.dayOfMonth,
                            hour,
                            minute
                        ).toInstant(TimeZone.currentSystemDefault())
                    }

                    val currentDateTime: Instant = Clock.System.now()

                    val task = TaskEntity(
                        title = title,
                        description = description,
                        isFavorite = favorite,
                        isCompleted = false,
                        hasTags = currentState.tagUuids.isNotEmpty(),
                        repeatType = repeatType,
                        notificationTime = dueDateTime,
                        parentListId = parentListId,
                    )

                    val generatedTaskId = taskDao.upsertTask(task).toInt()

                    if (currentState.tagUuids.isNotEmpty()) {
                        for (tagUuid in currentState.tagUuids) {
                            tagDao.insertTaskTag(
                                TaskTagsEntity(
                                    task.taskUuid,
                                    tagUuid,
                                    true,
                                    currentDateTime
                                )
                            )
                        }
                    }

                    if (repeatDbState.toSave) {
                        onRepeatDbEvent(SaveRepeat(generatedTaskId, task.taskUuid))
                    }

                    cleanUpOldLogs { cutoff -> deletedItemsDao.deleteLogsOlderThan(cutoff) }

                    if (dueDateTime != null) {
                        alarmScheduler.scheduleAlarm(
                            id = task.taskUuid.hashCode(),
                            uuid = task.taskUuid,
                            title = title,
                            description = description,
                            triggerAtMillis = dueDateTime.toEpochMilliseconds()
                        )
                    }

                    Log.d("Test", "Saved")
                    _state.update {
                        it.copy(
                            taskTitle = "",
                            todoDescription = "",
                            todoIsFavorite = false,

                            taskParentList = "",
                            taskParentListId = "",
                            tagUuids = emptyList(),

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
            is TaskDbEvent.SetTaskTitle -> {
                _state.update {
                    it.copy(
                        taskTitle = event.title
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
                viewModelScope.launch {
                    val listId = taskDao.getParentListId(event.listType)
                    _state.update {
                        it.copy(taskParentListId = listId ?: "")
                    }
                }
            }
            is TaskDbEvent.DeleteTaskByUuid -> {
                val currentDateTime: Instant = Clock.System.now()

                viewModelScope.launch {
                    val deletedTask = DeletedItemsEntity(
                        deletedUuid = event.uuid,
                        deletionTime = currentDateTime,
                        type = 1
                    )
                    deletedItemsDao.upsertDeletedItem(deletedTask)
                    taskDao.deleteTaskByUuid(event.uuid)

                    alarmScheduler.cancelAlarm(event.uuid)
                }
            }
            is TaskDbEvent.SetParentList -> {
                _state.update { it.copy(taskParentList = event.parentList) }
                viewModelScope.launch {
                    val listId = taskDao.getParentListId(event.parentList)
                    _state.update {
                        it.copy(
                            taskParentListId = listId ?: ""
                        )
                    }
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


            is TaskDbEvent.UpdateTaskTitleByUuid -> {
                viewModelScope.launch {
                    taskDao.updateTaskTitle(event.uuid, event.newTitle)
                    _state.update {
                        it.copy(
                            taskTitle = ""
                        )
                    }
                }
            }

            is TaskDbEvent.UpdateDescriptionByUuid -> {
                viewModelScope.launch {
                    taskDao.updateTaskDescription(event.uuid, event.newDescription)
                }
            }

            is TaskDbEvent.SetTaskTags -> {
                _state.update {
                    it.copy(
                        tagUuids = event.tagsUuid
                    )
                }
            }

            is TaskDbEvent.GetTaskById -> {
                viewModelScope.launch {
                    val taskIds = tagDao.getTasksByTagUuid(event.id)
                    val tasks = taskDao.getTaskById(taskIds)
                    _state.update {
                        it.copy(
                            tasksFromCurrentTag = tasks
                        )
                    }
                }
            }

            is TaskDbEvent.GetAllTasksByTagUuid -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            taskTags = taskDao.getAllTasksFromTagByUuid(event.uuid)
                        )
                    }
                }
            }

            is TaskDbEvent.UpdateTaskTag -> {
                viewModelScope.launch {
                    tagDao.upsertTaskTag(
                        TaskTagsEntity(
                            taskUuid = event.taskId,
                            tagUuid = event.tagId,
                            isActive = event.isActive,
                            updateTime = Clock.System.now()
                        )
                    )
                    _state.update { it.copy(taskTags = taskDao.getAllTasksFromTagByUuid(event.tagId)) }
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

            is TaskDbEvent.UpdateTaskDateTime -> {
                /**
                 * Updates the dueDateTime of a task to the selected date and time.
                 * - If there is no selected it uses the previews time. If the previews time was null it uses the default time which is 9:00
                 * - If there is no selected date it updates dueDateTime to null
                 */
                viewModelScope.launch {
                    val previewsDateTime = taskDao.getNotificationTimeByUuid(event.taskUuid)?.toLocalDateTime(TimeZone.currentSystemDefault())

                    val hour = if (_state.value.taskTimeHour != null) {
                        _state.value.taskTimeHour!!
                    } else previewsDateTime?.hour ?: 9

                    val minute = if (_state.value.taskTimeMinute != null) {
                        _state.value.taskTimeMinute!!
                    } else previewsDateTime?.minute ?: 0


                    val dueDateTime: Instant? = event.date?.let { timestamp ->
                        val date = Instant.fromEpochMilliseconds(timestamp)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        LocalDateTime(
                            date.year,
                            date.month,
                            date.dayOfMonth,

                            hour,
                            minute
                        ).toInstant(TimeZone.currentSystemDefault())
                    }
                    taskDao.updateTaskDateTime(event.taskUuid, dueDateTime)
                    _state.update {
                        it.copy(
                            taskTimeHour = null,
                            taskTimeMinute = null
                        )
                    }
                }
            }

            is TaskDbEvent.DeleteAllCompletedTasksByListId -> {
                viewModelScope.launch {
                    val completedTasks = taskDao.getAllCompletedTasksFromList(event.listId)
                    completedTasks.forEach { task ->
                        taskDao.deleteTask(task)
                        val deletedItem = DeletedItemsEntity(
                            type = 1,
                            deletedUuid = task.taskUuid
                        )
                        deletedItemsDao.upsertDeletedItem(deletedItem)
                    }
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