package com.eura.tasks

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.eura.tasks.notifications.FullDayNotificationService
import com.eura.tasks.notifications.FullDayTaskNotificationWorker
import java.util.concurrent.TimeUnit

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        createGeneralNotificationChannel()
        setupBackgroundWorker()
    }

    private fun createGeneralNotificationChannel() {

        val channel = NotificationChannel(
            FullDayNotificationService.GENERAL_CHANNEL_ID,
            "General",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "General task notifications"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun setupBackgroundWorker() {
        val repeatingWorkRequest = PeriodicWorkRequestBuilder<FullDayTaskNotificationWorker>(
            15, TimeUnit.MINUTES
        ).build()

        // ExistingPeriodicWorkPolicy.UPDATE ensures that if we change the request (e.g. interval), it gets updated
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "TaskNotificationWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            repeatingWorkRequest
        )
    }
}