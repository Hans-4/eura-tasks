package com.eura.tasks.ui.screens.searchScreen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eura.tasks.db.lists.UserListEntity

@Composable
fun ListItem(
    item: UserListEntity,
    onTaskList: (String) -> Unit,
) {
    Button(
        onClick = { onTaskList(item.title) },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text = item.title, fontSize = 18.sp)
    }
}