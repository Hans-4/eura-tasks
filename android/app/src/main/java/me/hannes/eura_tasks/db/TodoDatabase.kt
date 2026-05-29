package me.hannes.eura_tasks.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TodoEntity::class],
    version = 1
)
@TypeConverters(
    Converters::class
)
abstract class TodoDatabase: RoomDatabase() {
    abstract val dao: DbDao
}