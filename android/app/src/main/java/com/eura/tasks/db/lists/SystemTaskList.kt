package com.eura.tasks.db.lists

import java.util.UUID

data class SystemListItem(
    override val title: String,
    override val type: String,
    override val colorString: String,
    override val listId: String = UUID.randomUUID().toString(),
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
        title = "SYSTEM_WITH_TAGS",
        type = "WITH_TAGS",
        colorString = "BLUE"
    ),
)