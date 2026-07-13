package com.eura.tasks.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eura.tasks.db.deletedItems.DeletedItemsDao
import com.eura.tasks.db.deletedItems.DeletedItemsEntity
import com.eura.tasks.db.lists.ListDbDao
import com.eura.tasks.db.lists.UserListEntity
import com.eura.tasks.db.tags.TagDbDao
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.db.tasks.TaskDbDao
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.db.tasks.repeats.RepeatDbDao
import com.eura.tasks.db.tasks.repeats.RepeatEveryDayYearEntity
import com.eura.tasks.db.tasks.repeats.RepeatEveryMonthEntity
import com.eura.tasks.db.tasks.repeats.RepeatEveryWeekEntity
import com.eura.tasks.db.tasks.tags.TaskTagsEntity

@Database(
    entities = [
        TaskEntity::class,
        UserListEntity::class,
        TagsEntity::class,
        TaskTagsEntity::class,

        RepeatEveryDayYearEntity::class,
        RepeatEveryWeekEntity::class,
        RepeatEveryMonthEntity::class,

        DeletedItemsEntity::class,
    ],
    version = 1
)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase: RoomDatabase() {
    abstract val taskDao: TaskDbDao
    abstract val listDao: ListDbDao
    abstract val tagDao: TagDbDao
    abstract val repeatDao: RepeatDbDao
    abstract val deletedItemsDao: DeletedItemsDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}