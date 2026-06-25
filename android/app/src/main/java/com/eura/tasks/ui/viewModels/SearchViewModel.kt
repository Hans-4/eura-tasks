package com.eura.tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eura.tasks.db.SearchEvent
import com.eura.tasks.db.SearchFilter
import com.eura.tasks.db.SearchState
import com.eura.tasks.db.lists.ListDbDao
import com.eura.tasks.db.tags.TagDbDao
import com.eura.tasks.db.tasks.TaskDbDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class SearchViewModel(
    private val taskDao: TaskDbDao,
    private val listDao: ListDbDao,
    private val tagDao: TagDbDao
): ViewModel() {
    private val _searchFilter = MutableStateFlow(SearchFilter.TASK)

    private val _state = MutableStateFlow(SearchState())
    val state = combine(_state, _searchFilter) { state, searchFilter ->
        state.copy(
            searchFilter = searchFilter
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchState())

    fun onEvent(event: SearchEvent) {
        when(event) {
            is SearchEvent.SetSearchFilter -> {
                _searchFilter.value = event.searchFilter
            }
        }
    }
}