package com.eura.tasks.db.tasks.repeats

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface RepeatDbDao {
    @Upsert
    suspend fun upsertRepeatDay(repeat: RepeatEveryDayEntity)
    @Upsert
    suspend fun upsertRepeatWeek(repeat: RepeatEveryWeekEntity)
    @Upsert
    suspend fun upsertRepeatMonth(repeat: RepeatEveryMonthEntity)
    @Upsert
    suspend fun upsertRepeatYear(repeat: RepeatEveryYearEntity)
    @Upsert
    suspend fun upsertEndRepeat(repeat: EndRepeatsEntity)
}