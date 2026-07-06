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
            parentColumns = ["uuid"],
            childColumns = ["taskUuid"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RepeatEveryWeekEntity(
    val taskUuid: String,
    val startDate: Instant,
    val repeatEveryWeek: Int,
    val repeatDay: Int,
    val repeatTime: Instant?,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
