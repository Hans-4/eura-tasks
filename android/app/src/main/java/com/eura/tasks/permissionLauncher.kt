package com.eura.tasks

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.eura.tasks.ui.UiEvent

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun permissionLauncher(
    onUiEvent: (UiEvent) -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onUiEvent(UiEvent.SetNotificationPermissionState(isGranted))
    }

    LaunchedEffect(Unit) {
            val isGranted = context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            onUiEvent(UiEvent.SetNotificationPermissionState(isGranted))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                    val isGranted = context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                    onUiEvent(UiEvent.SetNotificationPermissionState(isGranted))
                }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return permissionLauncher
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CheckExactAlarmPermission(
    context: Context,
    onUiEvent: (UiEvent) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    fun checkPermission() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val isGranted = alarmManager.canScheduleExactAlarms()
        onUiEvent(UiEvent.SetAlarmPermissionState(isGranted))
    }

    // Check immediately when the composable enters the composition tree
    LaunchedEffect(Unit) {
        checkPermission()
    }

    // Monitor for lifecycle changes (especially ON_RESUME when coming back from settings)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            // Directs the system to open the settings specifically for your app package
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

fun openNotificationSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}


