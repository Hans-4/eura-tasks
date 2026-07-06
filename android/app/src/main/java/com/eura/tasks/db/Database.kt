package com.eura.tasks.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eura.tasks.db.lists.DeletedUserListEntity
import com.eura.tasks.db.lists.ListDbDao
import com.eura.tasks.db.lists.UserListEntity
import com.eura.tasks.db.tags.DeletedTagsEntity
import com.eura.tasks.db.tags.TagDbDao
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.DeletedTasksEntity
import com.eura.tasks.db.tasks.TaskDbDao
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.db.tasks.repeats.EndRepeatsEntity
import com.eura.tasks.db.tasks.repeats.RepeatDbDao
import com.eura.tasks.db.tasks.repeats.RepeatEveryDayEntity
import com.eura.tasks.db.tasks.repeats.RepeatEveryMonthEntity
import com.eura.tasks.db.tasks.repeats.RepeatEveryWeekEntity
import com.eura.tasks.db.tasks.repeats.RepeatEveryYearEntity
import com.eura.tasks.db.tasks.tags.DeletedTaskTagsEntity
import com.eura.tasks.db.tasks.tags.TaskTagsEntity

@Database(
    entities = [
        TaskEntity::class,
        DeletedTasksEntity::class,
        UserListEntity::class,
        DeletedUserListEntity::class,
        TagsEntity::class,
        DeletedTagsEntity::class,
        TaskTagsEntity::class,
        DeletedTaskTagsEntity::class,

        RepeatEveryDayEntity::class,
        RepeatEveryWeekEntity::class,
        RepeatEveryMonthEntity::class,
        RepeatEveryYearEntity::class,
        EndRepeatsEntity::class,
    ],
    version = 1
)
@TypeConverters(
    Converters::class
)
abstract class Database: RoomDatabase() {
    abstract val taskDao: TaskDbDao
    abstract val listDao: ListDbDao
    abstract val tagDao: TagDbDao
    abstract val repeatDao: RepeatDbDao
}