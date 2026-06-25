package com.eura.tasks.db.tags

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface TagDbDao {
    @Upsert
    suspend fun upsertTag(tag: TagsEntity)

    @Query("SELECT * FROM tags")
    fun getAllTags(): kotlinx.coroutines.flow.Flow<List<TagsEntity>>

    @Upsert
    suspend fun upsertDeletedTag(deletedTag: DeletedTagsEntity)
    @Query("INSERT INTO task_tags (taskUuid, tagUuid) VALUES (:taskUuid, :tagUuid)")
    suspend fun insertTaskTags(taskUuid: String, tagUuid: String)
    @Query("DELETE FROM task_tags WHERE taskUuid = :taskId")
    suspend fun deleteByTaskId(taskId: Int)
    @Query("DELETE FROM task_tags WHERE tagUuid = :tagId")
    suspend fun deleteByTagId(tagId: Int)
    @Query("SELECT EXISTS(SELECT 1 FROM tags WHERE LOWER(name) = LOWER(:title))")
    suspend fun searchForExistingTitle(title: String): Boolean
    @Query("DELETE FROM deleted_tags WHERE deletionDate < :cutoffTimestamp")
    suspend fun deleteLogsOlderThan(cutoffTimestamp: Long)
}