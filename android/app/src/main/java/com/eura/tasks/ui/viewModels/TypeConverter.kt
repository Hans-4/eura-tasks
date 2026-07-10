package com.eura.tasks.ui.viewModels

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TypeConverter {

    fun timestampToDateString(
        timestamp: Long
    ): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)

        // Convert to Local Date in UTC
        val localDate = instant.toLocalDateTime(TimeZone.UTC).date

        // Formatted to YYYY-MM-DD
        val formattedDate = localDate.toString()

        return formattedDate
    }

    fun timestampToCleanString(
        timestamp: Long,
        dateFormatter: DateTimeFormatter
    ): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)

        val localDate = instant.toLocalDateTime(TimeZone.UTC).date

        val javaLocalDate = localDate.toJavaLocalDate()

        // 3. Format it to "3. Jul. 2026"
        return javaLocalDate.format(dateFormatter)
    }

    fun formatInstant(instant: Instant): String {
        // 1. Convert Instant to LocalDateTime using a specific TimeZone
        val tz = TimeZone.currentSystemDefault() // Or TimeZone.UTC
        val localDateTime = instant.toLocalDateTime(tz)

        val formatter = DateTimeFormatter.ofPattern("HH:mm d.MMMM.yyyy", Locale.GERMAN)

        return localDateTime.toJavaLocalDateTime().format(formatter)
    }
}