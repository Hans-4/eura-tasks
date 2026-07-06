package com.eura.tasks.db.tasks.repeats

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.eura.tasks.db.tasks.TaskEntity
import kotlinx.datetime.Instant

@Entity(
    tableName = "end_repeats",
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
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EndRepeatsEntity(
    val taskUuid: String,
    val endsNever: Boolean,
    val endDate: Instant?,
    val endAfterRepetitions: Int?,
    @PrimaryKey
    val taskId: Int,
)
