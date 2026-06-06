package me.hannes.eura_tasks.db.lists

data class SystemListItem(
    val name: String,
    val type: String,
    val colorString: String,
)

val systemTaskList = listOf(
    SystemListItem(
        name = "SYSTEM_TODAY",
        type = "TODAY",
        colorString = "purple"
    ),
    SystemListItem(
        name = "SYSTEM_SCHEDULE",
        type = "SCHEDULE",
        colorString = "pink"
    ),
    SystemListItem(
        name = "SYSTEM_ALL",
        type = "ALL",
        colorString = "red"
    ),
    SystemListItem(
        name = "SYSTEM_FAVORITES",
        type = "FAVORITES",
        colorString = "yellow"
    ),
    SystemListItem(
        name = "SYSTEM_ASSIGNED_TO_ME",
        type = "ASSIGNED_TO_ME",
        colorString = "green"
    ),
    SystemListItem(
        name = "SYSTEM_GROCERIES",
        type = "GROCERIES",
        colorString = "blue"
    ),
    SystemListItem(
        name = "MY_TASKS",
        type = "OTHER",
        colorString = "purple"
    )
)