package com.eura.tasks.db

sealed interface SearchEvent {
    data class SetSearchFilter(val searchFilter: SearchFilter): SearchEvent
}