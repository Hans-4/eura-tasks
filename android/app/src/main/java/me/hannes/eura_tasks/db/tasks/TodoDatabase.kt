package me.hannes.eura_tasks.db.tasks

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.hannes.eura_tasks.db.Converters

@Database(
    entities = [TodoEntity::class, DeletedTasksEntity::class],
    version = 1
)
@TypeConverters(
    Converters::class
)
abstract class TodoDatabase: RoomDatabase() {
    abstract val dao: TaskDbDao
}