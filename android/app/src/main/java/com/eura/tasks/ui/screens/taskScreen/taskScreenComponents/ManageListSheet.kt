package com.eura.tasks.ui.screens.taskScreen.taskScreenComponents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eura.tasks.R
import com.eura.tasks.ui.SYSTEM_LISTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageListSheet(
    pageName: String,
    onDeleteAllCompletedTasks: () -> Unit,
    onRenameList: () -> Unit,
    onDeleteList: () -> Unit,
    onDismiss: () -> Unit
) {
    val isSystemList = pageName in SYSTEM_LISTS

    BackHandler(
        enabled = true,
        onBack = { onDismiss() }
    )

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        dragHandle = null,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onDeleteAllCompletedTasks() },
                text = "Delete all completed tasks"
            )

            Button(
                onClick = { onRenameList() },
                text = "Rename list"
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContentColor = MaterialTheme.colorScheme.outline
                ),
                enabled = !isSystemList,
                onClick = { onDeleteList() },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.delete_list),
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    if (isSystemList) {
                        Text(
                            text = stringResource(R.string.you_cant_delete_default_lists),
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Button(
    onClick: () -> Unit,
    text: String,
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.outline
        ),
        onClick = { onClick() },
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
        )
    }
}