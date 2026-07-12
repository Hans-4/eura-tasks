package com.eura.tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eura.tasks.db.cleanUpOldLogs
import com.eura.tasks.db.deletedItems.DeletedItemsDao
import com.eura.tasks.db.deletedItems.DeletedItemsEntity
import com.eura.tasks.db.tags.TagDbDao
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiEvent.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class TagDbViewModel(
    private val tagDao: TagDbDao,
    private val deletedItemsDao: DeletedItemsDao
): ViewModel()  {
    private val _state = MutableStateFlow(TagDbState())

    val state: StateFlow<TagDbState> = tagDao.getAllTags()
        .combine(_state) { tags, state ->
            state.copy(tags = tags)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TagDbState()
        )

    fun onEvent(event: TagDbEvent, onUiEvent: (UiEvent) -> Unit) {
        when(event) {
            TagDbEvent.SaveTagForTask -> {
                val title = _state.value.tagTitle.trim()
                if (title.isBlank()) return

                viewModelScope.launch {
                    if (tagDao.searchForExistingTitle(title)) {
                        onUiEvent(SetReason(2))
                        onUiEvent(OpenItemWithSimilarNameWarningDialog)
                    } else {
                        val now = Clock.System.now()
                        val tag = TagsEntity(
                            title = title,
                            creationTime = now,
                            updateTime = now
                        )
                        tagDao.upsertTag(tag)

                        _state.update {
                            it.copy(
                                tagTitle = "",
                                selectedTagUuids = it.selectedTagUuids + tag.tagUuid,
                            )
                        }
                        cleanUpOldLogs { cutoff -> deletedItemsDao.deleteLogsOlderThan(cutoff) }
                        onUiEvent(CloseAddTagTextField)
                    }
                }
            }

            is TagDbEvent.SaveTagInTask -> {
                val title = _state.value.tagTitle.trim()
                if (title.isBlank()) return

                viewModelScope.launch {
                    if (tagDao.searchForExistingTitle(title)) {
                        onUiEvent(SetReason(2))
                        onUiEvent(OpenItemWithSimilarNameWarningDialog)
                    } else {
                        val now = Clock.System.now()
                        val tag = TagsEntity(
                            title = title,
                            creationTime = now,
                            updateTime = now
                        )
                        tagDao.upsertTag(tag)

                        tagDao.insertTaskTag(
                            TaskTagsEntity(
                                taskUuid = event.taskId,
                                tagUuid = tag.tagUuid,
                                isActive = true,
                                updateTime = now
                            )
                        )
                        _state.update {
                            it.copy(
                                taskTags = it.taskTags + TaskTagsEntity(
                                    taskUuid = event.taskId,
                                    tagUuid = tag.tagUuid,
                                    isActive = true,
                                    updateTime = now
                                )
                            )
                        }

                        cleanUpOldLogs { cutoff -> deletedItemsDao.deleteLogsOlderThan(cutoff) }
                        onUiEvent(CloseAddTagsDialog)
                    }
                }
            }

            is TagDbEvent.SetTagTitle -> {
                _state.update {
                    it.copy(
                        tagTitle = event.title
                    )
                }
            }

            is TagDbEvent.SelectTag -> {
                _state.update {
                    it.copy(
                        selectedTagUuids = it.selectedTagUuids + event.id
                    )
                }
            }

            is TagDbEvent.UnselectTag -> {
                _state.update {
                    it.copy(
                        selectedTagUuids = it.selectedTagUuids - event.uuid
                    )
                }
            }

            TagDbEvent.UncheckAllTags -> {
                _state.update {
                    it.copy(
                        selectedTagUuids = emptyList(),
                    )
                }
            }

            is TagDbEvent.GetAllTagsByUuid -> {
                viewModelScope.launch {
                    val taskTagsEntity = tagDao.getAllTagsFromTask(event.uuid)
                    val tagsUuids = taskTagsEntity.filter { it.isActive }.map { it.tagUuid }
                    val allTags = tagDao.getTagsByUuids(tagsUuids)

                    _state.update {
                        it.copy(
                            tagsFromCurrentTask = allTags
                        )
                    }
                }
            }

            is TagDbEvent.GetAllTagsByTaskId -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            taskTags = tagDao.getAllTagsFromTaskById(event.taskId)
                        )
                    }
                }
            }

            is TagDbEvent.DeleteTag -> {
                val currentDateTime: Instant = Clock.System.now()

                viewModelScope.launch {
                    val deletedTag = DeletedItemsEntity(
                        deletedUuid = tagDao.getTagUuidById(event.tag.tagUuid),
                        deletionTime = currentDateTime,
                        type = 3
                    )
                    deletedItemsDao.upsertDeletedItem(deletedTag)
                    tagDao.deleteTag(event.tag)
                }
            }

            is TagDbEvent.InsertNewTaskTag -> {
                viewModelScope.launch {
                    tagDao.insertTaskTag(
                        TaskTagsEntity(
                            taskUuid = event.taskUuid,
                            tagUuid = event.tagUuid,
                            isActive = event.isActive,
                            updateTime = Clock.System.now()
                        )
                    )
                    _state.update {
                        it.copy(
                            taskTags = it.taskTags + TaskTagsEntity(
                                taskUuid = event.taskUuid,
                                tagUuid = event.tagUuid,
                                isActive = event.isActive,
                                updateTime = Clock.System.now()
                            )
                        )
                    }
                }
            }

            is TagDbEvent.UpdateTaskTag -> {
                viewModelScope.launch {
                    val entryExists = tagDao.searchForExistingEnty(event.taskUuid, event.tagId)
                    if (!entryExists) {
                        tagDao.upsertTaskTag(
                            TaskTagsEntity(
                                taskUuid = event.taskUuid,
                                tagUuid = event.tagId,
                                isActive = event.isActive,
                                updateTime = Clock.System.now()
                            )
                        )
                    } else {
                        tagDao.updateTaskTagActive(event.taskUuid, event.tagId, event.isActive)
                    }

                    _state.update {
                        it.copy(
                            taskTags = tagDao.getAllTagsFromTaskById(event.taskUuid)
                        )
                    }
                }
            }

            TagDbEvent.SaveTag -> {
                val title = _state.value.tagTitle.trim()
                if (title.isBlank()) return

                viewModelScope.launch {
                    if (tagDao.searchForExistingTitle(title)) {
                        onUiEvent(SetReason(2))
                        onUiEvent(OpenItemWithSimilarNameWarningDialog)
                    } else {
                        val now = Clock.System.now()
                        tagDao.upsertTag(
                            TagsEntity(
                                title = title,
                                creationTime = now,
                                updateTime = now
                            )
                        )

                        _state.update {
                            it.copy(
                                tagTitle = "",
                            )
                        }

                        onUiEvent(CloseAddTagsDialog)
                    }
                }
            }
        }
    }
}