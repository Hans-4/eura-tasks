package com.eura.tasks.db.tasks.repeats

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.eura.tasks.db.tasks.TaskEntity
import kotlinx.datetime.Instant

@Entity(
    tableName = "repeat_every_week",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["taskUuid"],
            childColumns = ["taskUuid"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RepeatEveryWeekEntity(
    val startDate: Instant,
    val repeatEveryWeek: Int,
    val repeatDay: Int,
    val minutesSinceMidnight: Int?,
    @PrimaryKey
    val taskUuid: String,
)
