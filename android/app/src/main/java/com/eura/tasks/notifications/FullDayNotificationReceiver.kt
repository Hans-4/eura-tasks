package com.eura.tasks.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eura.tasks.db.AppDatabase
import com.eura.tasks.notifications.FullDayNotificationService.Companion.ACTION_MARK_AS_COMPLETE
import com.eura.tasks.notifications.FullDayNotificationService.Companion.ACTION_RESCHEDULE_TOMORROW
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

class FullDayNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val id = intent?.getIntExtra("id", -1) ?: -1
        val action = intent?.action

        if (id == -1) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)

                when (action) {
                    ACTION_MARK_AS_COMPLETE -> {
                        db.taskDao.markAsCompleteById(id)
                    }
                    ACTION_RESCHEDULE_TOMORROW -> {
                        val timeZone = TimeZone.currentSystemDefault()
                        val today: LocalDate = Clock.System.todayIn(timeZone)
                        val tomorrow: LocalDate = today.plus(1, DateTimeUnit.DAY)
                        val tomorrowMidnightInstant: Instant = tomorrow.atStartOfDayIn(timeZone)
                        db.taskDao.updateTaskDateTime(id, tomorrowMidnightInstant)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}