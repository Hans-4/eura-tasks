package com.eura.tasks.db.tags

sealed interface TagDbEvent {
    object SaveTag: TagDbEvent
    data class SetTagTitle(val title: String): TagDbEvent
}