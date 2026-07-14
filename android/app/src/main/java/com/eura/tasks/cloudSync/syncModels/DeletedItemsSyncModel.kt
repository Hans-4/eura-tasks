package com.eura.tasks.cloudSync.syncModels

import kotlinx.datetime.Instant

data class DeletedItemsSyncModel(
    val id: String,
    val deletionTime: Instant,
    val type: Int
)
