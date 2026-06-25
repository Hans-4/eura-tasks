package com.eura.tasks.db.tags

sealed interface TagDbEvent {
    object SaveTag: TagDbEvent
    object UncheckAllTags: TagDbEvent
    data class SetTagTitle(val title: String): TagDbEvent
    data class SelectTag(val uuid: String): TagDbEvent
    data class UnselectTag(val uuid: String): TagDbEvent
}