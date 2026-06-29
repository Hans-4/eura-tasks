package com.eura.tasks.db.tags

sealed interface TagDbEvent {
    object SaveTag: TagDbEvent
    object UncheckAllTags: TagDbEvent
    data class SetTagTitle(val title: String): TagDbEvent
    data class SelectTag(val id: Int, val uuid: String): TagDbEvent
    data class UnselectTag(val id: Int, val uuid: String): TagDbEvent
    data class GetAllTagsByUuid(val uuid: String): TagDbEvent
    data class DeleteTag(val tag: TagsEntity): TagDbEvent
}