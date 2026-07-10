package com.eura.tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.eura.tasks.db.cleanUpOldLogs
import com.eura.tasks.db.deletedItems.DeletedItemsDao
import com.eura.tasks.db.deletedItems.DeletedItemsEntity
import com.eura.tasks.db.lists.ListDbDao
import com.eura.tasks.db.lists.ListDbEvent
import com.eura.tasks.db.lists.ListDbState
import com.eura.tasks.db.lists.UserListEntity
import com.eura.tasks.db.tasks.TaskDbDao
import com.eura.tasks.ui.UiEvent
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID



class ListDbViewModel(
    private val listDao: ListDbDao,
    private val taskDao: TaskDbDao,
    private val deletedItemsDao: DeletedItemsDao
): ViewModel() {
    private val _state = MutableStateFlow(ListDbState())
    val state = combine(_state, listDao.getAllLists()) { state, userLists ->
        state.copy(
            userLists = userLists
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListDbState())

    fun onEvent(event: ListDbEvent, onUiEvent: (UiEvent) -> Unit) {
        when(event) {
            ListDbEvent.SaveList ->  {
                val title = _state.value.listTitle.trim()
                val type = _state.value.listType
                val color = _state.value.listColor

                if (title.isBlank() || type.isBlank() || color.isBlank()) {
                    return
                }

                viewModelScope.launch {
                    if (listDao.searchForExistingTitle(title)) {
                        onUiEvent(UiEvent.SetReason(1))
                        onUiEvent(UiEvent.OpenItemWithSimilarNameWarningDialog)
                    } else {
                        val now = Clock.System.now()

                        val list = UserListEntity(
                            title = title,
                            type = type,
                            colorString = color,
                            creationTime = now,
                            updateTime = now,
                        )
                        listDao.upsertList(list)
                        _state.update {
                            it.copy(
                                listTitle = "",
                                listType = "OTHER",
                                listColor = "RED"
                            )
                        }
                        cleanUpOldLogs { cutoff -> deletedItemsDao.deleteLogsOlderThan(cutoff) }
                        onUiEvent(UiEvent.CloseAddTaskListDialog)
                    }
                }
            }
            is ListDbEvent.SetListColor -> {
                _state.update {
                    it.copy(
                        listColor = event.color
                    )
                }
            }
            is ListDbEvent.SetListTitle -> {
                _state.update {
                    it.copy(
                        listTitle = event.title
                    )
                }
            }
            is ListDbEvent.SetListType -> {
                _state.update {
                    it.copy(
                        listType = event.type
                    )
                }
            }
            is ListDbEvent.DeleteListByName -> {
                val currentDateTime: Instant = Clock.System.now()
                viewModelScope.launch {
                    val list = listDao.getListUuidByName(event.name)

                    val deletedList = DeletedItemsEntity(
                        deletedUuid = list.listId,
                        deletionTime = currentDateTime,
                        type = 1
                    )
                    taskDao.deleteTasksByListName(event.name)
                    deletedItemsDao.upsertDeletedItem(deletedList)
                    listDao.deleteList(list)
                }
            }
        }
    }

    /**
     * Helper for the Cloud Sync: Insert a list downloaded from Drive
     */
    fun insertList(
        name: String,
        type: String,
        color: String,
        uuid: String,
        creationTime: Instant,
        updateTime: Instant
    ) {
        viewModelScope.launch {
            val list = UserListEntity(
                title = name,
                type = type,
                colorString = color,
                creationTime = creationTime,
                updateTime = updateTime,
                listId = uuid
            )
            listDao.upsertList(list)
        }
    }

    /**
     * Synchronously inserts a missing list from cloud task processing thread context
     */
    suspend fun insertListSynchronously(
        name: String,
        type: String,
        color: String,
        creationTime: Instant,
        updateTime: Instant,
        uuid: String = UUID.randomUUID().toString()
    ) = withContext(Dispatchers.IO) {
        val list = UserListEntity(
            title = name,
            type = type,
            colorString = color,
            creationTime = creationTime,
            updateTime = updateTime,
            listId = uuid
        )
        listDao.upsertList(list)
    }
}