package com.eura.tasks.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

fun createGeneralNotificationChannel(context: Context) {
    val channelId = "general_channel"
    val channelName = "Task reminder"
    val descriptionText = "General task notifications"
    val importance = NotificationManager.IMPORTANCE_DEFAULT

    val channel = NotificationChannel(channelId, channelName, importance).apply {
        description = descriptionText
    }

    // Register the channel with the system
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}