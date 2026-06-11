package me.hannes.eura_tasks.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.hannes.eura_tasks.db.lists.DeletedUserListEntity
import me.hannes.eura_tasks.db.lists.ListDbDao
import me.hannes.eura_tasks.db.lists.UserListEntity
import me.hannes.eura_tasks.db.tasks.DeletedTasksEntity
import me.hannes.eura_tasks.db.tasks.TaskDbDao
import me.hannes.eura_tasks.db.tasks.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        DeletedTasksEntity::class,
        UserListEntity::class,
        DeletedUserListEntity::class
               ],
    version = 1
)
@TypeConverters(
    Converters::class
)
abstract class Database: RoomDatabase() {
    abstract val taskDao: TaskDbDao
    abstract val listDao: ListDbDao
}