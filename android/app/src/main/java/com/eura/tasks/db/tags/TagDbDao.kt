package com.eura.tasks.db.tags

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.eura.tasks.db.deletedItems.DeletedItemsEntity
import com.eura.tasks.db.tasks.tags.DeletedTaskTagsEntity
import com.eura.tasks.db.tasks.tags.TaskTagsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDbDao {
    @Upsert
    suspend fun upsertTag(tag: TagsEntity): Long
    @Delete
    suspend fun deleteTag(tag: TagsEntity)

    @Query("SELECT tagUuid FROM tags WHERE tagUuid = :uuid")
    suspend fun getTagUuidById(uuid: String): String

    @Query("SELECT * FROM tags")
    fun getAllTags(): Flow<List<TagsEntity>>

    @Query("SELECT * FROM task_tags")
    fun getAllTaskTags(): Flow<List<TaskTagsEntity>>
    @Query("SELECT * FROM deleted_task_tags")
    fun getAllDeletedTaskTags(): Flow<List<DeletedTaskTagsEntity>>
    
    @Query("SELECT * FROM deleted_items WHERE type = 3")
    fun getAllDeletedTags(): Flow<List<DeletedItemsEntity>>

    @Upsert
    suspend fun upsertDeletedTaskTag(deletedTaskTag: DeletedTaskTagsEntity)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskTag(taskTag: TaskTagsEntity)
    
    @Query("DELETE FROM task_tags WHERE taskUuid = :taskUuid")
    suspend fun deleteByTaskUuid(taskUuid: String)
    @Query("DELETE FROM task_tags WHERE tagUuid = :tagUuid")
    suspend fun deleteByTagUuid(tagUuid: String)

    @Query("DELETE FROM task_tags WHERE tagUuid = :tagId")
    suspend fun removeByTagId(tagId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM tags WHERE LOWER(title) = LOWER(:title))")
    suspend fun searchForExistingTitle(title: String): Boolean
    @Query("SELECT * FROM task_tags WHERE taskUuid = :taskUuid")
    suspend fun getAllTagsFromTask(taskUuid: String): List<TaskTagsEntity>
    @Query("SELECT * FROM task_tags WHERE taskUuid = :taskUuid")
    suspend fun getAllTagsFromTaskById(taskUuid: String): List<TaskTagsEntity>
    @Query("SELECT * FROM tags WHERE tagUuid IN (:uuids)")
    suspend fun getTagsByUuids(uuids: List<String>): List<TagsEntity>
    @Query("SELECT taskUuid FROM task_tags WHERE tagUuid = :tagUuid")
    suspend fun getTasksByTagUuid(tagUuid: String): List<String>
    @Query("SELECT * FROM tags WHERE LOWER(title) LIKE LOWER('%' || :query || '%')")
    suspend fun searchForTags(query: String): List<TagsEntity>
}