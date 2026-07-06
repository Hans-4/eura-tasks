package com.eura.tasks.db.tasks.repeats

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.eura.tasks.db.tasks.TaskEntity
import kotlinx.datetime.Instant

@Entity(
    tableName = "repeat_every_mont",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["uuid"],
            childColumns = ["taskUuid"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class RepeatEveryMonthEntity(
    val taskUuid: String,
    val startDate: Instant,
    val repeatEveryMonth: Int,
    val repeatDay: Int,
    val minutesSinceMidnight: Int?,
    @PrimaryKey
    val taskId: Int,
)
