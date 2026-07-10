package com.eura.tasks.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eura.tasks.db.AppDatabase
import com.eura.tasks.notifications.AlarmService.Companion.ACTION_MARK_AS_COMPLETE
import com.eura.tasks.notifications.AlarmService.Companion.ACTION_RESCHEDULE_TOMORROW
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        val id = intent.getIntExtra("id", -1)
        val uuid = intent.getStringExtra("uuid")
        val action = intent.action

        if (id == -1 || uuid == null) return

        val title = intent.getStringExtra("title") ?: "Task Reminder"
        val description = intent.getStringExtra("description") ?: ""

        // If it's a button action, dismiss the active notification banner
        if (action == ACTION_MARK_AS_COMPLETE || action == ACTION_RESCHEDULE_TOMORROW) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(id)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)

                when (action) {
                    ACTION_MARK_AS_COMPLETE -> {
                        db.taskDao.markAsCompleteByUuid(uuid)
                    }
                    ACTION_RESCHEDULE_TOMORROW -> {
                        val instant = db.taskDao.getNotificationTimeByUuid(uuid)
                        val instantIn24Hour = instant?.plus(24, DateTimeUnit.HOUR)
                        if (instantIn24Hour != null) {
                            db.taskDao.updateTaskDateTime(uuid, instantIn24Hour)

                            val scheduler = AlarmScheduler(context)
                            scheduler.scheduleAlarm(
                                id = id,
                                uuid = uuid,
                                title = title,
                                description = description,
                                triggerAtMillis = instantIn24Hour.toEpochMilliseconds()
                            )
                        }
                    }
                    else -> {
                        // MISSING STEP: The scheduled time hit! Present notification UI
                        val alarmService = AlarmService(context)
                        alarmService.showNotification(id, uuid, title, description)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}