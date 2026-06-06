package me.hannes.eura_tasks.db.dbFunctions

import me.hannes.eura_tasks.db.TaskDbDao
import java.time.Instant
import java.time.temporal.ChronoUnit

suspend fun cleanUpOldData(logDao: TaskDbDao) {
    // Calculate the point in time 30 days ago in UTC
    val thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()

    logDao.deleteLogsOlderThan(thirtyDaysAgo)
}