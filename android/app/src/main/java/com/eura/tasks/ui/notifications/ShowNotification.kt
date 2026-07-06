package com.eura.tasks.ui.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

fun showSimpleNotification(
    context: Context,
    title: String,
    message: String
) {
    val channelId = "general_channel"
    val notificationId = System.currentTimeMillis().toInt()

    // 1. Check for POST_NOTIFICATIONS permission on Android 13+ (API 33)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is missing, exit safely instead of crashing the app
            return
        }
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}