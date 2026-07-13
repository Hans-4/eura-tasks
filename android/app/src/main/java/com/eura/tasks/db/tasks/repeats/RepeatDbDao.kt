package com.eura.tasks.db.tasks.repeats

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RepeatDbDao {
    @Upsert
    suspend fun upsertRepeatDay(repeat: RepeatEveryDayYearEntity)
    @Upsert
    suspend fun upsertRepeatWeek(repeat: RepeatEveryWeekEntity)
    @Upsert
    suspend fun upsertRepeatMonth(repeat: RepeatEveryMonthEntity)

    @Query("SELECT * FROM repeat_every_day_year WHERE taskUuid = :taskUuid")
    suspend fun getRepeatDayYear(taskUuid: String): RepeatEveryDayYearEntity?
    @Query("SELECT * FROM repeat_every_week WHERE taskUuid = :taskUuid")
    suspend fun getRepeatWeek(taskUuid: String): RepeatEveryWeekEntity?
    @Query("SELECT * FROM repeat_every_mont WHERE taskUuid = :taskUuid")
    suspend fun getRepeatMonth(taskUuid: String): RepeatEveryMonthEntity?

    @Delete
    suspend fun removeRepeatDay(entity: RepeatEveryDayYearEntity)
    @Delete
    suspend fun removeRepeatWeek(entity: RepeatEveryWeekEntity)
    @Delete
    suspend fun removeRepeatMonth(entity: RepeatEveryMonthEntity)
}