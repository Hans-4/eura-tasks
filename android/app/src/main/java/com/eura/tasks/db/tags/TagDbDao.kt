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
    @Query("DELETE FROM task_tags WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: Int)
    @Query("DELETE FROM task_tags WHERE tagId = :tagId")
    suspend fun deleteByTagId(tagId: Int)
}