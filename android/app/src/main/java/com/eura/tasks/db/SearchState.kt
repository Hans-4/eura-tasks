package com.eura.tasks.db

data class SearchState(
    val searchQuery: String = "",
    val searchFilter: SearchFilter = SearchFilter.TASK,
)
