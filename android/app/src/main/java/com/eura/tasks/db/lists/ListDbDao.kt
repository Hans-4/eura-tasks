package com.eura.tasks.db.lists

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDbDao {
    @Upsert
    suspend fun upsertList(list: UserListEntity)
    @Update
    suspend fun updateList(list: UserListEntity)
    @Delete
    suspend fun deleteList(list: UserListEntity)
    @Query("SELECT * FROM user_lists")
    fun getAllTaskLists(): Flow<List<UserListEntity>>
    @Query("DELETE FROM user_lists WHERE listId = :uuid")
    suspend fun deleteListById(uuid: String)
    @Query("DELETE FROM user_lists WHERE title = :name")
    suspend fun deleteListByName(name: String)
    @Query("SELECT listId FROM user_lists WHERE listId = :uuid")
    suspend fun getListUuidById(uuid: String): String
    @Query("SELECT * FROM user_lists WHERE title = :name")
    suspend fun getListUuidByName(name: String): UserListEntity
    @Query("SELECT * FROM user_lists")
    fun getAllLists(): Flow<List<UserListEntity>>
    @Query("SELECT EXISTS(SELECT 1 FROM user_lists WHERE LOWER(title) = LOWER(:title))")
    suspend fun searchForExistingTitle(title: String): Boolean
    @Query("SELECT * FROM user_lists WHERE LOWER(title) LIKE LOWER('%' || :query || '%')")
    suspend fun searchForLists(query: String): List<UserListEntity>
}