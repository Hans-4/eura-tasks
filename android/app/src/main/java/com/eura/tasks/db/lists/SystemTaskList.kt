package com.eura.tasks.db.lists

data class SystemListItem(
    override val title: String,
    override val type: String,
    override val colorString: String,
) : TaskList

val systemTaskList = listOf(
    SystemListItem(
        title = "SYSTEM_TODAY",
        type = "TODAY",
        colorString = "PURPLE"
    ),
    SystemListItem(
        title = "SYSTEM_SCHEDULE",
        type = "SCHEDULE",
        colorString = "PINK"
    ),
    SystemListItem(
        title = "SYSTEM_ALL",
        type = "ALL",
        colorString = "RED"
    ),
    SystemListItem(
        title = "SYSTEM_FAVORITES",
        type = "FAVORITES",
        colorString = "YELLOW"
    ),
    SystemListItem(
        title = "SYSTEM_IMPORTANT",
        type = "IMPORTANT",
        colorString = "GREEN"
    ),
    SystemListItem(
        title = "SYSTEM_SHOPPING",
        type = "SHOPPING",
        colorString = "BLUE"
    ),
)