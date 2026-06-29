package com.eura.tasks.db.tags

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDbDao {
    @Upsert
    suspend fun upsertTag(tag: TagsEntity): Long

    @Query("SELECT * FROM tags")
    fun getAllTags(): Flow<List<TagsEntity>>

    @Query("SELECT * FROM task_tags")
    fun getAllTaskTags(): Flow<List<TaskTagsEntity>>
    
    @Query("SELECT * FROM deleted_tags")
    fun getAllDeletedTags(): Flow<List<DeletedTagsEntity>>

    @Upsert
    suspend fun upsertDeletedTag(deletedTag: DeletedTagsEntity)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskTag(taskTag: TaskTagsEntity)
    @Query("DELETE FROM task_tags WHERE taskUuid = :taskUuid")
    suspend fun deleteByTaskId(taskUuid: String)
    @Query("DELETE FROM task_tags WHERE tagUuid = :taskUuid")
    suspend fun deleteByTagId(taskUuid: String)
    @Query("SELECT EXISTS(SELECT 1 FROM tags WHERE LOWER(title) = LOWER(:title))")
    suspend fun searchForExistingTitle(title: String): Boolean
    @Query("DELETE FROM deleted_tags WHERE deletionDate < :cutoffTimestamp")
    suspend fun deleteLogsOlderThan(cutoffTimestamp: Long)
    @Query("SELECT * FROM task_tags WHERE taskUuid = :taskUuid")
    suspend fun getAllTagsFromTask(taskUuid: String): List<TaskTagsEntity>
    @Query("SELECT * FROM tags WHERE uuid IN (:uuids)")
    suspend fun getTagsByUuids(uuids: List<String>): List<TagsEntity>
    @Query("SELECT id FROM tags WHERE uuid = :uuid")
    suspend fun getTagIdByUuid(uuid: String): Int
    @Query("SELECT taskId FROM task_tags WHERE tagId = :tagId")
    suspend fun getTasksByTagId(tagId: Int): List<Int>
    @Query("SELECT * FROM tags WHERE LOWER(title) LIKE LOWER('%' || :query || '%')")
    suspend fun searchForTags(query: String): List<TagsEntity>
}