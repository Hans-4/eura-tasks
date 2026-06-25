package com.eura.tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eura.tasks.db.cleanUpOldLogs
import com.eura.tasks.db.tags.TagDbDao
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiEvent.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TagDbViewModel(
    private val tagDao: TagDbDao
): ViewModel()  {
    private val _state = MutableStateFlow(TagDbState())

    val state: StateFlow<TagDbState> = tagDao.getAllTags()
        .combine(_state) { tags, state ->
            state.copy(tags = tags)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TagDbState())

    fun onEvent(event: TagDbEvent, onUiEvent: (UiEvent) -> Unit) {
        when(event) {
            TagDbEvent.SaveTag -> {
                val title = _state.value.tagTitle.trim()

                if (title.isBlank()) {
                    return
                }

                viewModelScope.launch {
                    if (tagDao.searchForExistingTitle(title)) {
                        onUiEvent(SetReason(2))
                        onUiEvent(OpenItemWithSimilarNameWarningDialog)
                    } else {
                        val tag = TagsEntity(
                            name = title,
                        )

                        tagDao.upsertTag(tag)

                        _state.update {
                            it.copy(
                                tagTitle = "",
                                selectedTagUuids = it.selectedTagUuids + tag.uuid,
                            )
                        }

                        cleanUpOldLogs { cutoff -> tagDao.deleteLogsOlderThan(cutoff) }
                        onUiEvent(CloseAddTagTextField)
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
                        selectedTagUuids = it.selectedTagUuids + event.uuid
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
                        selectedTagUuids = emptyList()
                    )
                }
            }
        }
    }
}