package com.eura.tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import com.eura.tasks.db.tasks.repeats.RepeatDbDao
import com.eura.tasks.db.tasks.repeats.RepeatDbEvent
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
        }
    }
}