package me.hannes.eura_tasks.db.lists

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
        name = "SYSTEM_ASSIGNED_TO_ME",
        type = "ASSIGNED_TO_ME",
        colorString = "GREEN"
    ),
    SystemListItem(
        name = "SYSTEM_GROCERIES",
        type = "GROCERIES",
        colorString = "BLUE"
    ),
)