package com.eura.tasks.db.tags

sealed interface TagDbEvent {
    object SaveTagForTask: TagDbEvent
    object SaveTag: TagDbEvent
    data class SaveTagInTask(val taskId: String,): TagDbEvent
    object UncheckAllTags: TagDbEvent
    data class SetTagTitle(val title: String): TagDbEvent
    data class SelectTag(val id: String): TagDbEvent
    data class UnselectTag(val uuid: String): TagDbEvent
    data class GetAllTagsByUuid(val uuid: String): TagDbEvent
    data class GetAllTagsByTaskId(val taskId: String): TagDbEvent
    data class DeleteTag(val tag: TagsEntity): TagDbEvent
    data class InsertNewTaskTag(val taskUuid: String, val tagUuid: String): TagDbEvent
    data class RemoveFromTaskByTagId(val tagId: String): TagDbEvent
}