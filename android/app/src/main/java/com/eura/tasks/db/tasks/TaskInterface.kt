package com.eura.tasks.db.tasks

import kotlinx.datetime.Instant

/**
 * Contains the values from the [TaskEntity]
 */
interface TaskInterface {
    val title: String
    val description: String?
    val isFavorite: Boolean
    val isCompleted: Boolean
    val hasTags: Boolean
    val repeatType: Int?
    val notificationTime: Instant?

    val parentListId: String

    val creationTime: Instant
    val updateTime: Instant

    val taskUuid: String
}