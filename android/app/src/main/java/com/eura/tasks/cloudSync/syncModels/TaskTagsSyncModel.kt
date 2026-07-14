package com.eura.tasks.cloudSync.syncModels

import kotlinx.datetime.Instant

data class TaskTagsSyncModel(
    val taskUuid: String,
    val tagUuid: String,
    val isActive: Boolean,
    val updateTime: Instant
)
