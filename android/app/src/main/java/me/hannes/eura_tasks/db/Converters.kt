package me.hannes.eura_tasks.db

import androidx.room.TypeConverter
import kotlin.time.Instant

class Converters {
    @TypeConverter
    fun timestampToDate(value: Long?): Instant? {
        return value?.let {
            Instant.Companion.fromEpochMilliseconds(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.toEpochMilliseconds()
    }
}