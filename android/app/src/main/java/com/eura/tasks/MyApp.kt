package com.eura.tasks

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.eura.tasks.ui.notifications.CounterNotificationService

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        createGeneralNotificationChannel()
    }

    private fun createGeneralNotificationChannel() {

        val channel = NotificationChannel(
            CounterNotificationService.COUNTER_CHANNEL_ID,
            "General",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "General task notifications"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}