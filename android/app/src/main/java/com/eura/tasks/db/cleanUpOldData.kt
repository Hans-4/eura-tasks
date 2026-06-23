package com.eura.tasks.db

import com.eura.tasks.db.lists.ListDbDao
import com.eura.tasks.db.tasks.TaskDbDao
import java.time.Instant
import java.time.temporal.ChronoUnit

suspend fun cleanUpOldTasks(taskDao: TaskDbDao) {
    val cutoffTimestamp = Instant.now().minus(120, ChronoUnit.DAYS).toEpochMilli()
    taskDao.deleteLogsOlderThan(cutoffTimestamp)
}

suspend fun cleanUpOldLists(listDao: ListDbDao) {
    val cutoffTimestamp = Instant.now().minus(120, ChronoUnit.DAYS).toEpochMilli()
    listDao.deleteLogsOlderThan(cutoffTimestamp)
}
