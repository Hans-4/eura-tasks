package com.eura.tasks.notifications.repeats

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class RepeatsWorker(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}