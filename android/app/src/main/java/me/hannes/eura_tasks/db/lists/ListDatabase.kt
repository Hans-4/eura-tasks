package me.hannes.eura_tasks.db.lists

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.hannes.eura_tasks.db.Converters

@Database(
    entities = [UserListEntity::class, DeletedUserListEntity::class],
    version = 1
)
@TypeConverters(
    Converters::class
)
abstract class ListDatabase: RoomDatabase() {
    abstract val dao: ListDbDao
}