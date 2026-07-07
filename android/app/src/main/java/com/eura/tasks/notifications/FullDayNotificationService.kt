package com.eura.tasks.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
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
        val activityIntent = Intent(
            Intent.ACTION_VIEW,
            "euratasks://taskdetails/$id/Notification".toUri(),
            context,
            MainActivity::class.java
        )

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            id, // Use the task ID as request code to keep them unique
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val markAsCompleteIntent = PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, FullDayNotificationReceiver::class.java).apply {
                action = ACTION_MARK_AS_COMPLETE
                putExtra("id", id)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val rescheduleIntent = PendingIntent.getBroadcast(
            context,
            id + 1000000,
            Intent(context, FullDayNotificationReceiver::class.java).apply {
                action = ACTION_RESCHEDULE_TOMORROW
                putExtra("id", id)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, GENERAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Mark completed",
                markAsCompleteIntent
                )
            .addAction(//TODO
                R.drawable.ic_launcher_foreground,
                "Reschedule to tomorrow",
                rescheduleIntent
            )
            .build()
        notificationManager.notify(id, notification)
    }

    companion object {
        const val GENERAL_CHANNEL_ID = "general_channel"
        const val ACTION_MARK_AS_COMPLETE = "ACTION_MARK_AS_COMPLETE"
        const val ACTION_RESCHEDULE_TOMORROW = "ACTION_RESCHEDULE_TOMORROW"
    }
}