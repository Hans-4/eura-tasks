package com.eura.tasks.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // This code runs the EXACT millisecond the alarm goes off!
        // We just call your existing notification function.
        showSimpleNotification(
            context,
            "Reminder",
            "This is a reminder!"
        )
    }
}