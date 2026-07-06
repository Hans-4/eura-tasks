package com.eura.tasks.notifications

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eura.tasks.db.AppDatabase
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

class FullDayTaskNotificationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Build a separate instance of your DB inside the background process
            val db = Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "database.db"
            ).build()

            // 2. Fetch the tasks using your custom DAO query
            val fullDayTasks = db.taskDao.getAllFullDayTasks()

            // 3. Trigger a notification if uncompleted tasks exist
            if (fullDayTasks.isNotEmpty()) {
                val service = FullDayNotificationService(appContext)

                val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

                fullDayTasks.forEach { task ->
                    val taskDate = task.dueDateTime?.toLocalDateTime(TimeZone.currentSystemDefault())?.date
                    if (taskDate == currentDate) {
                        service.showNotification(
                            id = task.id,
                            taskTitle = task.title,
                            taskDescription = task.description
                        )
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}