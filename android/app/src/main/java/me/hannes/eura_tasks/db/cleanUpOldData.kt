package me.hannes.eura_tasks.db

import me.hannes.eura_tasks.db.lists.ListDbDao
import me.hannes.eura_tasks.db.tasks.TaskDbDao
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
