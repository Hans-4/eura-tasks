package com.eura.tasks.ui.screens.taskScreen.taskScreenComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eura.tasks.db.lists.ListDbEvent
import com.eura.tasks.db.lists.ListDbState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameListDialog(
    listTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,

    onListDbEvent: (ListDbEvent) -> Unit,
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = listTitle,
                selection = TextRange(listTitle.length)
            )
        )
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        onListDbEvent(ListDbEvent.SetUpdateListTitle(listTitle))
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { onDismiss() }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Back"
                            )
                        }
                    },
                    title = { Text("Rename list") },
                    actions = {
                        TextButton(
                            onClick = { onConfirm() }
                        ) {
                            Text("Save")
                        }
                    }
                )
            }
        ) { innerPadding ->
            TextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    onListDbEvent(ListDbEvent.SetUpdateListTitle(it.text))
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .padding(innerPadding)
                    .fillMaxWidth(),
                placeholder = { Text("New list name") }
            )
        }
    }
}