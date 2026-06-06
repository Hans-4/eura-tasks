package me.hannes.eura_tasks.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.hannes.eura_tasks.db.DeletedTasksEntity
import me.hannes.eura_tasks.db.TaskDbDao
import me.hannes.eura_tasks.db.TodoEntity

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