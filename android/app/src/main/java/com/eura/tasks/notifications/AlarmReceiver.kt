package com.eura.tasks.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eura.tasks.db.AppDatabase
import com.eura.tasks.db.tasks.TaskEntity
import com.eura.tasks.notifications.AlarmService.Companion.ACTION_MARK_AS_COMPLETE
import com.eura.tasks.notifications.AlarmService.Companion.ACTION_RESCHEDULE_TOMORROW
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class AlarmReceiver(private val alarmScheduler: AlarmScheduler) : BroadcastReceiver() {

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


                        val task = db.taskDao.getTaskById(uuid)

                        val dayRepeatEntry = db.repeatDao.getRepeatDayYear(uuid)
                        val weekRepeatEntry = db.repeatDao.getRepeatWeek(uuid)
                        val monthRepeatEntry = db.repeatDao.getRepeatMonth(uuid)

                        if (task != null) {
                            if (task.repeatType != null) {
                                val triggerAtMillis = when (task.repeatType) {
                                    1 -> {
                                        val timeZone = TimeZone.currentSystemDefault()
                                        val today = Clock.System.now().toLocalDateTime(timeZone).date
                                        val tomorrow = today.plus(1, DateTimeUnit.DAY)

                                        val tomorrowMidnightMillis = tomorrow.atStartOfDayIn(timeZone).toEpochMilliseconds()

                                        db.repeatDao.dayReduceRemainingRepeats(taskUuid = uuid, currentTime = Clock.System.now())
                                        
                                        tomorrowMidnightMillis + dayRepeatEntry!!.minutesSinceMidnight!!.toLong() * 10000
                                    }
                                    2 -> return@launch
                                    3 -> return@launch
                                    else -> return@launch
                                }

                                val newTask = TaskEntity(
                                    title = task.title,
                                    description = task.description,
                                    isFavorite = task.isFavorite,
                                    isCompleted = false,
                                    hasTags = task.hasTags,
                                    repeatType = task.repeatType,
                                    parentListId = task.parentListId,
                                    parentTaskUuid = dayRepeatEntry.taskUuid //TODO: Update to use the specific repeat type entry
                                )

                                db.taskDao.upsertTask(newTask)
                                val id = newTask.taskUuid

                                alarmScheduler.scheduleAlarm(
                                    id = id.hashCode(),
                                    uuid = id,
                                    title = title,
                                    description = description,
                                    triggerAtMillis = triggerAtMillis
                                )
                            }
                        }
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