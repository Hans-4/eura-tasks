package com.eura.tasks.notifications

import android.content.Context
import android.util.Log
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
        Log.d("FullDayTaskWorker", "Worker started")
        return try {
            val db = AppDatabase.getDatabase(appContext)

            val fullDayTasks = db.taskDao.getAllFullDayTasks()
            Log.d("FullDayTaskWorker", "Found ${fullDayTasks.size} full day tasks")

            if (fullDayTasks.isNotEmpty()) {
                val service = FullDayNotificationService(appContext)
                val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

                fullDayTasks.forEach { task ->
                    val taskDate = task.dueDateTime?.toLocalDateTime(TimeZone.currentSystemDefault())?.date
                    if (taskDate == currentDate) {
                        Log.d("FullDayTaskWorker", "Showing notification for task: ${task.title}")
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
            Log.e("FullDayTaskWorker", "Error in worker", e)
            Result.retry()
        }
    }
}