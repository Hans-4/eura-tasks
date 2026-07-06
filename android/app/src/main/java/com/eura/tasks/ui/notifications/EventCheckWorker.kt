package com.eura.tasks.ui.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eura.tasks.db.AppDatabase
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class EventCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val nowLocal = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val todayStr = nowLocal.date.toString()

        if (nowLocal.hour >= 8) {
            val sharedPrefs = applicationContext.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
            val lastDate = sharedPrefs.getString("last_notified_date", "")
            val notifiedIds = if (lastDate == todayStr) {
                sharedPrefs.getStringSet("notified_ids", emptySet()) ?: emptySet()
            } else {
                emptySet()
            }


            val db = AppDatabase.getDatabase(applicationContext)
            val tasks = db.taskDao.getAllFullDayTasks()
            val todayUTC = Clock.System.now().toLocalDateTime(TimeZone.UTC).date

            tasks.forEach { task ->
                val taskDate = task.dueDateTime?.toLocalDateTime(TimeZone.UTC)?.date
                val isAlreadyNotified = notifiedIds.contains(task.id.toString())

                if (taskDate == todayUTC && !isAlreadyNotified) {
                    showSimpleNotification(applicationContext, "Aufgabe für heute", task.title)

                    // ID zur Liste hinzufügen und speichern
                    val newIds = notifiedIds.toMutableSet()
                    newIds.add(task.id.toString())
                    sharedPrefs.edit()
                        .putString("last_notified_date", todayStr)
                        .putStringSet("notified_ids", newIds)
                        .apply()
                }
            }
        }
        return Result.success()
    }
}