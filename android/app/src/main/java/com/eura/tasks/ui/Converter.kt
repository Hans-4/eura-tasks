package com.eura.tasks.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.LocalMovies
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Today
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.eura.tasks.R
import com.eura.tasks.db.tasks.repeats.RepeatDbState
import com.eura.tasks.ui.theme.ColorItems
import com.eura.tasks.ui.theme.blue
import com.eura.tasks.ui.theme.green
import com.eura.tasks.ui.theme.pink
import com.eura.tasks.ui.theme.purple
import com.eura.tasks.ui.theme.red
import com.eura.tasks.ui.theme.yellow

class Converter() {
    companion object {
        @Composable
        fun pageNameConverter(
            pageName: String
        ): String {
             return when (pageName) {
                 "SYSTEM_TODAY" -> stringResource(R.string.today)
                 "SYSTEM_SCHEDULE" -> "Schedule"
                 "SYSTEM_ALL" -> stringResource(R.string.all)
                 "SYSTEM_FAVORITES" -> stringResource(R.string.favorites)
                 "SYSTEM_IMPORTANT" -> stringResource(R.string.important)
                 "SYSTEM_WITH_TAGS" -> stringResource(R.string.with_tags)
                 "MY_TASKS" -> stringResource(R.string.my_tasks)
                 else -> pageName
            }
        }

        @Composable
        fun systemTypeConverter(typeString: String): ImageVector {
            return when (typeString) {
                "TODAY" -> Icons.Rounded.Today
                "SCHEDULE" -> Icons.Rounded.Event
                "ALL" -> Icons.AutoMirrored.Rounded.List
                "FAVORITES" -> Icons.Rounded.Star
                "IMPORTANT" -> Icons.Rounded.PriorityHigh
                "WITH_TAGS" -> Icons.Rounded.Sell
                else -> Icons.Outlined.BugReport
            }
        }
        @Composable
        fun typeIconConverter(typeString: String): ImageVector {
            return when (typeString) {
                "REMINDERS" -> Icons.Outlined.Notifications
                "TRAVEL" -> Icons.Outlined.Flight
                "FINANCE" -> Icons.Outlined.MonetizationOn
                "SHOPPING" -> Icons.Outlined.ShoppingCart
                "WORK" -> Icons.Outlined.Work
                "HEALTH" -> Icons.Outlined.FavoriteBorder
                "MEDIA" -> Icons.Outlined.LocalMovies
                "OTHER" -> Icons.Outlined.Checklist
                else -> Icons.Outlined.BugReport
            }
        }

        @Composable
        fun colorStringConverter(
            systemThemeIndex: Int,
            colorString: String?
        ): ColorItems {
            val red = red[systemThemeIndex]
            val yellow = yellow[systemThemeIndex]
            val green = green[systemThemeIndex]
            val blue = blue[systemThemeIndex]
            val purple = purple[systemThemeIndex]
            val pink = pink[systemThemeIndex]

            return when(colorString) {
                "RED" -> red
                "YELLOW" -> yellow
                "GREEN" -> green
                "BLUE" -> blue
                "PINK" -> pink
                else -> purple
            }
        }

        @Composable
        fun taskRepeatInfoString(
            repeatDbState: RepeatDbState
        ): String {
            val hour = if (repeatDbState.repeatTimeHour == null)
                null else if (repeatDbState.repeatTimeHour < 10)
                    "0${repeatDbState.repeatTimeHour}"
            else
                repeatDbState.repeatTimeHour

            val minute = if (repeatDbState.repeatTimeMinute == null)
                null
            else if (repeatDbState.repeatTimeMinute < 10)
                "0${repeatDbState.repeatTimeMinute}"
            else
                repeatDbState.repeatTimeMinute

            val timeText = if (hour != null && minute != null) ", at ${hour}:${minute}" else ""

            val text = when (repeatDbState.selectedRepeatType) {
                0 -> {
                    if (repeatDbState.repeatEvery.toInt() != 1)
                        "Every ${repeatDbState.repeatEvery} days"
                    else
                        "Daily"
                }
                1 -> {
                    "Weekly"
                }
                2 -> {
                    "Monthly"
                }
                3 -> {
                    if (repeatDbState.repeatEvery.toInt() != 1)
                        "Every ${repeatDbState.repeatEvery} years"
                    else
                        "Yearly"
                }
                else -> {
                    ""
                }
            }

            val endText = when (repeatDbState.selectedRadioButton) {
                0 -> ""
                1 -> ", until ${repeatDbState.endDateString}"
                2 -> ", ${repeatDbState.endAfterRepeats} times"
                else -> ""
            }

            return "${text}${timeText}${endText}"
        }
    }
}