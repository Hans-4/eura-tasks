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
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val db = AppDatabase.getDatabase(context)
            val scheduler = AlarmScheduler(context)
            val now = Clock.System.now().toEpochMilliseconds()

            CoroutineScope(Dispatchers.IO).launch {
                val clock: Clock = Clock.System

                val pendingTasks = db.taskDao.getAllActiveTasksWithAlarms(clock.now())

                pendingTasks.forEach { task ->
                    val triggerTime = task.dueDateTime?.toEpochMilliseconds() ?: 0
                    if (triggerTime > now) {
                        scheduler.scheduleAlarm(
                            id = task.id,
                            title = task.title,
                            description = task.description,
                            triggerAtMillis = triggerTime
                        )
                    }
                }
            }
        }
    }
}