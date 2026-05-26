package me.hannes.eura_todo.ui.screens.homeScreenComponents.AddTaskBottomSheetComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ShortText
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.hannes.eura_todo.R
import me.hannes.eura_todo.db.DbEvent
import me.hannes.eura_todo.db.DbState
import me.hannes.eura_todo.ui.UiEvent

@Composable
fun AddTaskScreen(
    onDbEvent: (DbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    dbState: DbState,
    currentTab: String,
    firstTaskList: String,
    onNavigateToSelectTaskListScreen: () -> Unit,
) {
    val isFavorite = dbState.todoIsFavorite
    val isLockedAsFavorite = currentTab == "FAVOURITES"

    LaunchedEffect(currentTab) {
        if (isLockedAsFavorite) {
            onDbEvent(DbEvent.SetTodoIsFavorite(isFavorite = true, todo = null))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (currentTab == "FAVOURITES") {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onNavigateToSelectTaskListScreen() },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    if (dbState.taskParentList.isBlank()) firstTaskList else dbState.taskParentList
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null
                )
            }
        }

        val parenList = if (currentTab == "FAVOURITES") {
            if (dbState.taskParentList.isBlank()) firstTaskList else dbState.taskParentList
        } else currentTab

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = dbState.todoTitle,
            onValueChange = {
                onDbEvent(DbEvent.SetTodoTitle(it))
            },
            placeholder = {
                Text(text = "Title")
            },
            singleLine = true
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = dbState.todoDescription,
            onValueChange = {
                onDbEvent(DbEvent.SetTodoDescription(it))
            },
            placeholder = {
                Text(text = "Description")
            }
        )

        Row(

        ) {
            IconButton(
                onClick = { TODO() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShortText,
                    contentDescription = "Add description button"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    onDbEvent(DbEvent.SetTodoIsFavorite(isFavorite = !isFavorite, todo = null))
                },
                enabled = !isLockedAsFavorite
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = null,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = {
                    onDbEvent(DbEvent.SetParentList(parenList))
                    onDbEvent(DbEvent.SaveTask)
                }
            ) {
                Text(
                    stringResource(R.string.save)
                )
            }
        }
    }
}