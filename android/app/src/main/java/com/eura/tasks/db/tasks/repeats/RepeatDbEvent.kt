package com.eura.tasks.db.tasks.repeats

sealed interface RepeatDbEvent {
    data class SetSelectedRepeatType(val type: Int): RepeatDbEvent
    data class SetRepeatIntervals(val intervals: String): RepeatDbEvent
    data class SetRepeatTime(val hour: Int?, val minute: Int?): RepeatDbEvent
    data class SetStartDate(val date: Long): RepeatDbEvent
    data class SetEndDate(val date: Long): RepeatDbEvent
    data class SetRepeatEnd(val days: String): RepeatDbEvent

    data class SetRepeatEndType(val type: Int): RepeatDbEvent
}