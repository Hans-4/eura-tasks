package com.eura.tasks.db.deletedItems

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DeletedItemsDao {
    @Upsert
    suspend fun upsertDeletedItem(deletedItem: DeletedItemsEntity)

    @Query("SELECT * FROM deleted_items")
    fun getAllDeletedItems(): Flow<List<DeletedItemsEntity>>

    @Query("DELETE FROM deleted_items WHERE deletionTime < :cutoffTimestamp")
    suspend fun deleteLogsOlderThan(cutoffTimestamp: Long)
}