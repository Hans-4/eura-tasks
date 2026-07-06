package com.eura.tasks.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.eura.tasks.MainActivity
import com.eura.tasks.R

class FullDayNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(
        id: Int,
        taskTitle: String,
        taskDescription: String
    ) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            id,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val incrementIntent = PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, FullDayNotificationReceiver::class.java).apply {
                putExtra("id", id)
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, COUNTER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setContentIntent(activityPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Mark completed",
                incrementIntent
                )
            .build()
        notificationManager.notify(id, notification)
    }

    companion object {
        const val COUNTER_CHANNEL_ID = "general_channel"
    }
}