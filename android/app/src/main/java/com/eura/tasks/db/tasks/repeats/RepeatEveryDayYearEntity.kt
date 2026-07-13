package com.eura.tasks.db.tasks.repeats

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.eura.tasks.db.tasks.TaskEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * @property endAfterRepetitions The number of repetitions after which the repeat ends. This number will decrease every time it repeats.
 */
@Entity(
    tableName = "repeat_every_day_year",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["taskUuid"],
            childColumns = ["taskUuid"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RepeatEveryDayYearEntity(
    val startDate: Instant,
    val repeatEveryDay: Int,
    val minutesSinceMidnight: Int?,
    val period: Int, //0: Day 3: Year,
    val lastRepeat: Instant? = null,

    val endsNever: Boolean,
    val endDate: Instant?,
    val endAfterRepetitions: Int?,

    val updateTime: Instant = Clock.System.now(),
    @PrimaryKey
    val taskUuid: String,
)
