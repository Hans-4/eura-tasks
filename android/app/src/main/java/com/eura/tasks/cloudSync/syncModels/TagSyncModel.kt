package com.eura.tasks.cloudSync.syncModels

import kotlinx.datetime.Instant

data class TagSyncModel(
    val uuid: String,
    val name: String,
    val creationTime: Instant,
    val updateTime: Instant
)
