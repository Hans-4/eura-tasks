package com.eura.tasks.db

import java.time.Instant
import java.time.temporal.ChronoUnit

suspend fun cleanUpOldLogs(deleteAction: suspend (Long) -> Unit) {
    val cutoffTimestamp = Instant.now().minus(120, ChronoUnit.DAYS).toEpochMilli()
    deleteAction(cutoffTimestamp)
}
