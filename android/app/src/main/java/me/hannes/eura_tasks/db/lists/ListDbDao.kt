package me.hannes.eura_tasks.db.lists

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface ListDbDao {
    @Upsert
    suspend fun upsertList(list: UserListEntity)
    @Upsert
    suspend fun upsertDeletedList(deletedList: DeletedUserListEntity)
    @Update
    suspend fun update(list: UserListEntity)
    @Query("DELETE FROM user_lists WHERE id = :id")
    suspend fun deleteListById(id: Int)
    @Query("DELETE FROM user_lists WHERE name = :name")
    suspend fun deleteListByName(name: String)
    @Query("SELECT uuid FROM user_lists WHERE id = :id")
    suspend fun getListUuidById(id: Int): String
    @Query("SELECT uuid FROM user_lists WHERE name = :name")
    suspend fun getListUuidByName(name: String): String
    @Query("SELECT * FROM user_lists")
    fun getAllLists(): kotlinx.coroutines.flow.Flow<List<UserListEntity>>
    @Query("SELECT * FROM deleted_user_lists")
    fun getAllDeletedLists(): kotlinx.coroutines.flow.Flow<List<DeletedUserListEntity>>
    @Query("DELETE FROM deleted_user_lists WHERE deletionDate < :cutoffTimestamp")
    suspend fun deleteLogsOlderThan(cutoffTimestamp: Long)
    @Query("SELECT EXISTS(SELECT 1 FROM user_lists WHERE LOWER(name) = LOWER(:title))")
    suspend fun searchForExistingTitle(title: String): Boolean
    @Query("SELECT EXISTS(SELECT 1 FROM user_lists WHERE uuid = :uuid)")
    suspend fun listExists(uuid: String): Boolean
    @Query("SELECT EXISTS(SELECT 1 FROM deleted_user_lists WHERE deletedUuid = :uuid)")
    suspend fun deleted(uuid: String): Boolean
}