package me.hannes.eura_todo.ui

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
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Today
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import me.hannes.eura_todo.R
import me.hannes.eura_todo.ui.theme.ColorItems
import me.hannes.eura_todo.ui.theme.blue
import me.hannes.eura_todo.ui.theme.green
import me.hannes.eura_todo.ui.theme.pink
import me.hannes.eura_todo.ui.theme.purple
import me.hannes.eura_todo.ui.theme.red
import me.hannes.eura_todo.ui.theme.yellow

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
                 "SYSTEM_ASSIGNED_TO_ME" -> "Assigned to me"
                 "SYSTEM_GROCERIES" -> "Groceries"
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
                "ASSIGNED_TO_ME" -> Icons.Rounded.PersonAdd
                "GROCERIES" -> Icons.Rounded.ShoppingCart
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
                "red" -> red
                "yellow" -> yellow
                "green" -> green
                "blue" -> blue
                "pink" -> pink
                else -> purple
            }
        }
    }
}