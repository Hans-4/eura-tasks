package com.eura.tasks.db.tasks.repeats

data class RepeatDbState(
    val dayRepeats: List<RepeatEveryDayEntity> = emptyList(),
    val weekRepeats: List<RepeatEveryWeekEntity> = emptyList(),
    val monthRepeats: List<RepeatEveryMonthEntity> = emptyList(),
    val yearRepeats: List<RepeatEveryYearEntity> = emptyList(),
    val endRepeats: List<EndRepeatsEntity> = emptyList(),

    val selectedRepeatType: Int = 1,
    val repeatEvery: String = "1",
    val repeatTimeHour: Int? = null,
    val repeatTimeMinute: Int? = null,
    val startDate: Long? = null,
    val startDateString: String? = null,

    val selectedRadioButton: Int = 1, //1 = Never, 2 = On, 3 = After
    val endDate: Long? = null,
    val endDateString: String? = null,
    val endAfterRepeats: String = "7"
)
