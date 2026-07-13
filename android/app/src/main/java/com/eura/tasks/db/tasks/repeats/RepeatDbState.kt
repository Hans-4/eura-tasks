package com.eura.tasks.db.tasks.repeats

data class RepeatDbState(
    val dayRepeats: List<RepeatEveryDayYearEntity> = emptyList(),
    val weekRepeats: List<RepeatEveryWeekEntity> = emptyList(),
    val monthRepeats: List<RepeatEveryMonthEntity> = emptyList(),

    val selectedRepeatType: Int = 1, //0: Day, 1: Week, 2: Month, 3: Year
    val repeatEvery: String = "1",
    val repeatTimeHour: Int? = null,
    val repeatTimeMinute: Int? = null,
    val startDate: Long? = null,
    val startDateString: String? = null,

    val selectedRadioButton: Int = 0, //0 = Never, 1 = On, 2 = After
    val endDate: Long? = null,
    val endDateString: String? = null,
    val endAfterRepeats: String = "7",

    val toSave: Boolean = false
)
