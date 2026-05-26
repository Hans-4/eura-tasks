package me.hannes.eura_todo.db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import kotlin.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
class Converters {
    @TypeConverter
    fun timestampToDate(value: Long?): Instant? {
        return value?.let {
            Instant.fromEpochMilliseconds(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.toEpochMilliseconds()
    }
}