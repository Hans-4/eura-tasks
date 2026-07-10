package com.eura.tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eura.tasks.db.tasks.repeats.EndRepeatsEntity
import com.eura.tasks.db.tasks.repeats.RepeatDbDao
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import com.eura.tasks.db.tasks.repeats.RepeatEveryDayEntity
import com.eura.tasks.db.tasks.repeats.RepeatEveryMonthEntity
import com.eura.tasks.db.tasks.repeats.RepeatEveryWeekEntity
import com.eura.tasks.db.tasks.repeats.RepeatEveryYearEntity
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
                val taskId = event.taskId

                val instant = Instant.fromEpochMilliseconds(_state.value.startDate!!)

                val repeatEvery = _state.value.repeatEvery.toInt()
                val minutesSinceMidnight = (_state.value.repeatTimeHour ?: 0) * 60 + (_state.value.repeatTimeMinute ?: 0)

                val endDate = _state.value.endDate
                val endAfterRepeats = _state.value.endAfterRepeats

                viewModelScope.launch {
                    when (_state.value.selectedRepeatType) {
                        0 -> repeatDao.upsertRepeatDay(
                            RepeatEveryDayEntity(
                                taskUuid = taskUuid,
                                startDate = instant,
                                repeatEveryDay = repeatEvery,
                                minutesSinceMidnight = minutesSinceMidnight,
                                taskId = taskId
                            )
                        )

                        1 -> repeatDao.upsertRepeatWeek(
                            RepeatEveryWeekEntity(
                                taskUuid = taskUuid,
                                startDate = instant,
                                repeatEveryWeek = repeatEvery,
                                repeatDay = repeatEvery,
                                minutesSinceMidnight = minutesSinceMidnight,
                                taskId = taskId
                            )
                        )

                        2 -> repeatDao.upsertRepeatMonth(
                            RepeatEveryMonthEntity(
                                taskUuid = taskUuid,
                                startDate = instant,
                                repeatEveryMonth = repeatEvery,
                                repeatDay = repeatEvery,
                                minutesSinceMidnight = minutesSinceMidnight,
                                taskId = taskId
                            )
                        )

                        3 -> repeatDao.upsertRepeatYear(
                            RepeatEveryYearEntity(
                                taskUuid = taskUuid,
                                startDate = instant,
                                repeatEveryYear = repeatEvery,
                                minutesSinceMidnight = minutesSinceMidnight,
                                taskId = taskId
                            )
                        )
                    }

                    val endDateInstant = Instant.fromEpochMilliseconds(endDate!!)

                    when (_state.value.selectedRadioButton) {
                        0 -> repeatDao.upsertEndRepeat(
                            EndRepeatsEntity(
                                taskUuid = taskUuid,
                                endsNever = true,
                                endDate = null,
                                endAfterRepetitions = null,
                                taskId = taskId
                            )
                        )

                        1 -> repeatDao.upsertEndRepeat(
                            EndRepeatsEntity(
                                taskUuid = taskUuid,
                                endsNever = false,
                                endDate = endDateInstant,
                                endAfterRepetitions = null,
                                taskId = taskId
                            )
                        )

                        2 -> repeatDao.upsertEndRepeat(
                            EndRepeatsEntity(
                                taskUuid = taskUuid,
                                endsNever = false,
                                endDate = null,
                                endAfterRepetitions = endAfterRepeats.toInt(),
                                taskId = taskId
                            )
                        )
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
        }
    }
}