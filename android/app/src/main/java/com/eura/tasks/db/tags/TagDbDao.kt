package com.eura.tasks.db.tags

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDbDao {
    @Upsert
    suspend fun upsertTag(tag: TagsEntity)

    @Query("SELECT * FROM tags")
    fun getAllTags(): Flow<List<TagsEntity>>

    @Upsert
    suspend fun upsertDeletedTag(deletedTag: DeletedTagsEntity)
    @Query("INSERT INTO task_tags (taskUuid, tagUuid) VALUES (:taskUuid, :tagUuid)")
    suspend fun insertTaskTags(taskUuid: String, tagUuid: String)
    @Query("DELETE FROM task_tags WHERE taskUuid = :taskUuid")
    suspend fun deleteByTaskId(taskUuid: String)
    @Query("DELETE FROM task_tags WHERE tagUuid = :taskUuid")
    suspend fun deleteByTagId(taskUuid: String)
    @Query("SELECT EXISTS(SELECT 1 FROM tags WHERE LOWER(name) = LOWER(:title))")
    suspend fun searchForExistingTitle(title: String): Boolean
    @Query("DELETE FROM deleted_tags WHERE deletionDate < :cutoffTimestamp")
    suspend fun deleteLogsOlderThan(cutoffTimestamp: Long)
    @Query("SELECT * FROM task_tags WHERE taskUuid = :taskUuid")
    suspend fun getAllTagsFromTask(taskUuid: String): List<TaskTagsEntity>
    @Query("SELECT * FROM tags WHERE uuid IN (:uuids)")
    suspend fun getTagsByUuids(uuids: List<String>): List<TagsEntity>
}