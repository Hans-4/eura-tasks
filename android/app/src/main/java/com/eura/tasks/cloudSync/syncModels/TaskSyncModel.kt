package com.eura.tasks.cloudSync.syncModels

import kotlinx.datetime.Instant

data class TaskSyncModel(
    val uuid: String,
    val title: String,
    val description: String?,
    val isFavorite: Boolean,
    val isCompleted: Boolean,
    val hasTags: Boolean,
    val parentListId: String,
    val dueDateTime: Instant?,
    val creationTime: Instant,
    val updateTime: Instant
)