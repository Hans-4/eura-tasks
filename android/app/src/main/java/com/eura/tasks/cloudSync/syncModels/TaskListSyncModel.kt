package com.eura.tasks.cloudSync.syncModels

import kotlinx.datetime.Instant

data class TaskListSyncModel(
    val id: String,
    val title: String,
    val type: String,
    val color: String,
    val creationTime: Instant,
    val updateTime: Instant
)
