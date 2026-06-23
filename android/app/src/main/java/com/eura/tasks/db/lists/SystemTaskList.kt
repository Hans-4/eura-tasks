package com.eura.tasks.db.lists

data class SystemListItem(
    override val name: String,
    override val type: String,
    override val colorString: String,
) : TaskList

val systemTaskList = listOf(
    SystemListItem(
        name = "SYSTEM_TODAY",
        type = "TODAY",
        colorString = "PURPLE"
    ),
    SystemListItem(
        name = "SYSTEM_SCHEDULE",
        type = "SCHEDULE",
        colorString = "PINK"
    ),
    SystemListItem(
        name = "SYSTEM_ALL",
        type = "ALL",
        colorString = "RED"
    ),
    SystemListItem(
        name = "SYSTEM_FAVORITES",
        type = "FAVORITES",
        colorString = "YELLOW"
    ),
    SystemListItem(
        name = "SYSTEM_IMPORTANT",
        type = "IMPORTANT",
        colorString = "GREEN"
    ),
    SystemListItem(
        name = "SYSTEM_SHOPPING",
        type = "SHOPPING",
        colorString = "BLUE"
    ),
)