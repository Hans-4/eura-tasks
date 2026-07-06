package com.eura.tasks.ui.notifications

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

@Composable
fun NotificationTriggerButton(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current

    // This launcher handles the native, default system dialog callback
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted() // Call your notification function here
        }
    }

    Button(onClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if we already have it
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                onPermissionGranted()
            } else {
                // This triggers the default Android system dialog instantly
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Android 12 and below don't need runtime permission
            onPermissionGranted()
        }
    }) {
        Text("Trigger Notification")
    }
}