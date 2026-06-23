package com.eura.tasks.ui.screens.homeScreenComponents

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
import com.eura.tasks.db.lists.TaskList
import com.eura.tasks.db.tasks.TaskDbState
import com.eura.tasks.R
import com.eura.tasks.ui.Converter

@Composable
fun UserListColumnItem(
    noUserList: Boolean,
    taskLists: List<TaskList>,
    systemThemeIndex: Int,
    taskDbState: TaskDbState,
    onTaskList: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
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
                    val itemName = Converter.pageNameConverter(item.name)
                    val taskListCount = taskDbState.tasks.filter { it.taskList == item.name }.size

                    UserTaskLists(
                        index = index,
                        title = itemName,
                        icon = icon,
                        count = taskListCount,
                        color = colorItem,
                        onClick = { onTaskList(item.name) }
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