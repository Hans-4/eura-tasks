package com.eura.tasks

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.eura.tasks.notifications.AlarmService

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        createImportantNotificationChannel()
        createTimedNotificationChannel()
    }

    private fun createImportantNotificationChannel() {

        val channel = NotificationChannel(
            AlarmService.IMPORTANT_CHANNEL_ID,
            "Important",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Important tasks notifications"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createTimedNotificationChannel() {

        val channel = NotificationChannel(
            AlarmService.ALARM_CHANNEL_ID,
            "Timed",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Timed task notifications"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}