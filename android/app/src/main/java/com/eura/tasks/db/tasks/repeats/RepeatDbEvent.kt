package com.eura.tasks.db.tasks.repeats

sealed interface RepeatDbEvent {
    object ResetState: RepeatDbEvent
    data class SaveRepeat(val taskId: Int, val taskUuid: String): RepeatDbEvent
    data class SetSelectedRepeatType(val type: Int): RepeatDbEvent
    data class SetRepeatIntervals(val intervals: String): RepeatDbEvent
    data class SetRepeatTime(val hour: Int?, val minute: Int?): RepeatDbEvent
    data class SetStartDate(val date: Long): RepeatDbEvent
    data class SetEndDate(val date: Long): RepeatDbEvent
    data class SetRepeatEnd(val days: String): RepeatDbEvent

    data class SetRepeatEndType(val type: Int): RepeatDbEvent

    object SetToSave: RepeatDbEvent
    object RemoveToSave: RepeatDbEvent

    data class RemoveRepeat(val taskUuid: String): RepeatDbEvent
}