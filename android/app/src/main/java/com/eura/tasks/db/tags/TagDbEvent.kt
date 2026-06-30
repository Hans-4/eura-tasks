package com.eura.tasks.db.tags

sealed interface TagDbEvent {
    object SaveTagForTask: TagDbEvent
    data class SaveTagInTask(val taskId: Int, val taskUuid: String): TagDbEvent
    object UncheckAllTags: TagDbEvent
    data class SetTagTitle(val title: String): TagDbEvent
    data class SelectTag(val id: Int, val uuid: String): TagDbEvent
    data class UnselectTag(val id: Int, val uuid: String): TagDbEvent
    data class GetAllTagsByUuid(val uuid: String): TagDbEvent
    data class GetAllTagsByTaskId(val taskId: Int): TagDbEvent
    data class DeleteTag(val tag: TagsEntity): TagDbEvent
    data class InsertNewTaskTag(val taskId: Int, val taskUuid: String, val tagId: Int, val tagUuid: String): TagDbEvent
    data class RemoveFromTaskByTagId(val tagId: Int): TagDbEvent
}