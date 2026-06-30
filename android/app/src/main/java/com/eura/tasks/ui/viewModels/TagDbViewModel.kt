package com.eura.tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eura.tasks.db.cleanUpOldLogs
import com.eura.tasks.db.tags.DeletedTagsEntity
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
import kotlin.time.Clock
import kotlin.time.Instant

class TagDbViewModel(
    private val tagDao: TagDbDao
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
                        val tag = TagsEntity(title = title)
                        val generatedId = tagDao.upsertTag(tag).toInt()

                        _state.update {
                            it.copy(
                                tagTitle = "",
                                selectedTagUuids = it.selectedTagUuids + tag.uuid,
                                selectedTagIds = it.selectedTagIds + generatedId
                            )
                        }
                        cleanUpOldLogs { cutoff -> tagDao.deleteLogsOlderThan(cutoff) }
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
                        val tag = TagsEntity(title = title)

                        val tagId = tagDao.upsertTag(tag).toInt()

                        tagDao.insertTaskTag(
                            TaskTagsEntity(
                                taskId = event.taskId,
                                taskUuid = event.taskUuid,
                                tagId = tagId,
                                tagUuid = tag.uuid
                            )
                        )
                        _state.update {
                            it.copy(
                                taskTags = it.taskTags + TaskTagsEntity(
                                    taskId = event.taskId,
                                    taskUuid = event.taskUuid,
                                    tagId = tagId,
                                    tagUuid = event.taskUuid
                                )
                            )
                        }

                        cleanUpOldLogs { cutoff -> tagDao.deleteLogsOlderThan(cutoff) }
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
                        selectedTagIds = it.selectedTagIds + event.id,
                        selectedTagUuids = it.selectedTagUuids + event.uuid
                    )
                }
            }

            is TagDbEvent.UnselectTag -> {
                _state.update {
                    it.copy(
                        selectedTagIds = it.selectedTagIds - event.id,
                        selectedTagUuids = it.selectedTagUuids - event.uuid
                    )
                }
            }

            TagDbEvent.UncheckAllTags -> {
                _state.update {
                    it.copy(
                        selectedTagUuids = emptyList(),
                        selectedTagIds = emptyList()
                    )
                }
            }

            is TagDbEvent.GetAllTagsByUuid -> {
                //TODO: Improve this
                viewModelScope.launch {
                    val taskTagsEntity = tagDao.getAllTagsFromTask(event.uuid)
                    val tagsUuids = taskTagsEntity.map { it.tagUuid }
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
                    val deletedTag = DeletedTagsEntity(
                        deletedUuid = tagDao.getTagUuidById(event.tag.id),
                        deletionDate = currentDateTime
                    )
                    tagDao.upsertDeletedTag(deletedTag)
                    tagDao.deleteTag(event.tag)
                }
            }

            is TagDbEvent.InsertNewTaskTag -> {
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

            is TagDbEvent.RemoveFromTaskByTagId -> {
                viewModelScope.launch {
                    tagDao.removeByTagId(event.tagId)
                    _state.update { it ->
                        it.copy(
                            taskTags = it.taskTags.filter { it.tagId != event.tagId }
                        )
                    }
                }
            }
        }
    }
}