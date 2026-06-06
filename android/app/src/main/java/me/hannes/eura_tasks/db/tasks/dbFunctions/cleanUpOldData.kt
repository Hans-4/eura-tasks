package me.hannes.eura_tasks.db.tasks.dbFunctions

import me.hannes.eura_tasks.db.tasks.TaskDbDao
import java.time.Instant
import java.time.temporal.ChronoUnit

suspend fun cleanUpOldData(logDao: TaskDbDao) {
    // Calculate the point in time 120 days ago in UTC
    val thirtyDaysAgo = Instant.now().minus(120, ChronoUnit.DAYS).toEpochMilli()

    logDao.deleteLogsOlderThan(thirtyDaysAgo)
}