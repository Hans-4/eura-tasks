package me.hannes.eura_todo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.hannes.eura_todo.R

@Composable
fun pageNameConverter(
    pageName: String
): String {
    val pagenName = when (pageName) {
        "SYSTEM_TODAY" -> stringResource(R.string.today)
        "SYSTEM_SCHEDULE" -> "Schedule"
        "SYSTEM_ALL" -> stringResource(R.string.all)
        "SYSTEM_FAVORITES" -> stringResource(R.string.favorites)
        "SYSTEM_ASSIGNED_TO_ME" -> "Assigned to me"
        "SYSTEM_GROCERIES" -> "Groceries"
        else -> pageName
    }

    return pagenName
}