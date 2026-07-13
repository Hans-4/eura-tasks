package com.eura.tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eura.tasks.db.tasks.repeats.RepeatDbDao
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import com.eura.tasks.db.tasks.repeats.RepeatEveryDayYearEntity
import com.eura.tasks.db.tasks.repeats.RepeatEveryMonthEntity
import com.eura.tasks.db.tasks.repeats.RepeatEveryWeekEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale


class RepeatDbViewModel(
    private val repeatDao: RepeatDbDao
): ViewModel() {
    private val _state = MutableStateFlow(RepeatDbState())

    private val _converter = TypeConverter()

    val state: MutableStateFlow<RepeatDbState> = _state

    fun onEvent(event: RepeatDbEvent) {
        when(event) {
            is RepeatDbEvent.SaveRepeat -> {
                val taskUuid = event.taskUuid

                val startDate = _state.value.startDate ?: return
                val instant = Instant.fromEpochMilliseconds(startDate)

                val repeatEvery = _state.value.repeatEvery.toIntOrNull() ?: 1
                val minutesSinceMidnight = (_state.value.repeatTimeHour ?: 0) * 60 + (_state.value.repeatTimeMinute ?: 0)

                val endDate = _state.value.endDate
                val endAfterRepeats = _state.value.endAfterRepeats

                viewModelScope.launch {
                    val endsRadiobutton = _state.value.selectedRadioButton
                    val endsNever = if (endsRadiobutton == 0) true else false

                    val endDateInstant = endDate?.let { Instant.fromEpochMilliseconds(it) }
                    val endDate = if (endsRadiobutton == 1) endDateInstant else null

                    val endAfterRepetitions = if (endsRadiobutton == 2) endAfterRepeats.toIntOrNull() else null

                    when (_state.value.selectedRepeatType) {
                        0 -> repeatDao.upsertRepeatDay(
                            RepeatEveryDayYearEntity(
                                taskUuid = taskUuid,
                                startDate = instant,
                                repeatEveryDay = repeatEvery,
                                minutesSinceMidnight = minutesSinceMidnight,
                                period = _state.value.selectedRepeatType,

                                endsNever = endsNever,
                                endDate = endDate,
                                endAfterRepetitions = endAfterRepetitions,
                            )
                        )

                        1 -> repeatDao.upsertRepeatWeek(
                            RepeatEveryWeekEntity(
                                taskUuid = taskUuid,
                                startDate = instant,
                                repeatEveryWeek = repeatEvery,
                                repeatDay = repeatEvery,
                                minutesSinceMidnight = minutesSinceMidnight,

                                endsNever = endsNever,
                                endDate = endDate,
                                endAfterRepetitions = endAfterRepetitions,
                            )
                        )

                        2 -> repeatDao.upsertRepeatMonth(
                            RepeatEveryMonthEntity(
                                taskUuid = taskUuid,
                                startDate = instant,
                                repeatEveryMonth = repeatEvery,
                                repeatDay = repeatEvery,
                                minutesSinceMidnight = minutesSinceMidnight,

                                endsNever = endsNever,
                                endDate = endDate,
                                endAfterRepetitions = endAfterRepetitions,
                            )
                        )
                    }
                    _state.update {
                        RepeatDbState()
                    }
                }
            }

            RepeatDbEvent.ResetState -> {
                _state.update {
                    RepeatDbState()
                }
            }

            is RepeatDbEvent.SetSelectedRepeatType -> {
                _state.update {
                    it.copy(
                        selectedRepeatType = event.type
                    )
                }
            }

            is RepeatDbEvent.SetRepeatTime -> {
                _state.update {
                    it.copy(
                        repeatTimeHour = event.hour,
                        repeatTimeMinute = event.minute
                    )
                }
            }

            is RepeatDbEvent.SetStartDate -> {
                _state.update {
                    it.copy(
                        startDate = event.date,

                        startDateString = _converter.timestampToCleanString(
                            timestamp = event.date,
                            dateFormatter = DateTimeFormatter.ofPattern("d. MMM. yyyy", Locale.GERMANY)
                        )
                    )
                }
            }

            is RepeatDbEvent.SetEndDate -> {
                _state.update {
                    it.copy(
                        endDate = event.date,

                        endDateString = _converter.timestampToCleanString(
                            timestamp = event.date,
                            dateFormatter = DateTimeFormatter.ofPattern("d. MMM. yyyy", Locale.GERMANY)
                        )
                    )
                }
            }

            is RepeatDbEvent.SetRepeatIntervals -> {
                _state.update {
                    it.copy(
                        repeatEvery = event.intervals
                    )
                }
            }

            is RepeatDbEvent.SetRepeatEnd -> {
                _state.update {
                    it.copy(
                        endAfterRepeats = event.days
                    )
                }
            }

            is RepeatDbEvent.SetRepeatEndType -> {
                _state.update {
                    it.copy(
                        selectedRadioButton = event.type
                    )
                }
            }

            RepeatDbEvent.SetToSave -> {
                _state.update {
                    it.copy(
                        toSave = true
                    )
                }
            }

            is RepeatDbEvent.RemoveRepeat -> {
                viewModelScope.launch {
                    val existsDayYear = repeatDao.getRepeatDayYear(event.taskUuid)
                    if (existsDayYear != null) {
                        repeatDao.removeRepeatDay(existsDayYear)
                    } else {
                        val existsWeek = repeatDao.getRepeatWeek(event.taskUuid)
                        if (existsWeek != null) {
                            repeatDao.removeRepeatWeek(existsWeek)
                        } else {
                            val existsMonth = repeatDao.getRepeatMonth(event.taskUuid)
                            if (existsMonth != null) {
                                repeatDao.removeRepeatMonth(existsMonth)
                            }
                        }
                    }
                }
            }
        }
    }
}