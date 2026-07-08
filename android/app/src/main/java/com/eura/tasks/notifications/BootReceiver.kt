package com.eura.tasks.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eura.tasks.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action?.contains("QUICKBOOT_POWERON") == true) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val scheduler = AlarmScheduler(context)
                    val now = Clock.System.now()

                    val pendingTasks = db.taskDao.getAllActiveTasksWithAlarms(now)
                    pendingTasks.forEach { task ->
                        val triggerTime = task.dueDateTime?.toEpochMilliseconds() ?: 0
                        scheduler.scheduleAlarm(
                            id = task.id,
                            title = task.title,
                            description = task.description,
                            triggerAtMillis = triggerTime
                        )
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}