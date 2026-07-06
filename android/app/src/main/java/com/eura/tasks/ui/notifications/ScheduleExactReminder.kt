package com.eura.tasks.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

fun scheduleExactReminder(context: Context, triggerTimeInMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Create an intent pointing to your AlarmReceiver
    val intent = Intent(context, AlarmReceiver::class.java)

    // Wrap it in a PendingIntent so the OS can fire it on your app's behalf
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0, // Unique ID for this specific alarm
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Android 12+ check: Make sure the app is permitted to schedule exact alarms
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            // Redirect user to system settings if they revoked this permission
            val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(settingsIntent)
            return
        }
    }

    // Schedule the exact alarm.
    // alarmClock behavior ensures it wakes the device up even in "Doze" power-saving mode.
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerTimeInMillis,
        pendingIntent
    )
}