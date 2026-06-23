package com.eura.tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eura.tasks.db.tags.TagDbDao
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tags.TagsEntity
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

    fun onEvent(event: TagDbEvent) {
        when(event) {
            TagDbEvent.SaveTag -> {
                val title = _state.value.tagTitle.trim()

                if (title.isBlank()) {
                    return
                }

                val tag = TagsEntity(
                    name = title,
                )
                viewModelScope.launch {
                    tagDao.upsertTag(tag)
                    _state.update {
                        it.copy(
                            tagTitle = ""
                        )
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
        }
    }
}