package com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.userListColumn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eura.tasks.db.lists.TaskListInterface
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.R
import com.eura.tasks.ui.Converter
import com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.userListColumn.components.UserTaskLists

@Composable
fun UserListColumnItem(
    noUserList: Boolean,
    taskLists: List<TaskListInterface>,
    systemThemeIndex: Int,
    taskDbState: TaskDbState,
    onTaskList: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.my_lists),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (!noUserList) {
            Card(
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(
                    width = 1.dp,
                    brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                taskLists.forEachIndexed { index, item ->
                    val icon = Converter.typeIconConverter(typeString = item.type)
                    val colorItem = Converter.colorStringConverter(
                        systemThemeIndex = systemThemeIndex,
                        colorString = item.colorString
                    )
                    val itemName = Converter.pageNameConverter(item.title)
                    val taskListCount = taskDbState.tasks.filter { it.parentListId == item.listId }.size

                    UserTaskLists(
                        index = index,
                        title = itemName,
                        icon = icon,
                        count = taskListCount,
                        color = colorItem,
                        onClick = { onTaskList(item.listId, item.title) }
                    )
                }
            }
        } else {
            Text(
                text = "No Lists",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 104.dp)
                    .fillMaxWidth()
            )
        }
    }
}