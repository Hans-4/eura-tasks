package com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.addTask.addTaskComponents

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eura.tasks.R
import com.eura.tasks.db.tags.TagDbEvent
import com.eura.tasks.db.tags.TagDbState
import com.eura.tasks.db.tasks.TaskDbEvent
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.UiState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddTagsDialog(
    onClose: () -> Unit,
    onTagDbEvent: (TagDbEvent) -> Unit,
    tagDbState: TagDbState,
    onTaskDbEvent: (TaskDbEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    uiState: UiState
) {

    val tagList = tagDbState.tags
    val isEmpty = tagList.isEmpty()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(uiState.isAddTagTextFieldOpen) {
        if (uiState.isAddTagTextFieldOpen) {
            focusRequester.requestFocus()
        }
    }

    AlertDialog(
        onDismissRequest = { onClose() },
        confirmButton = {
            TextButton(
                onClick = {
                    onClose()
                    onTaskDbEvent(TaskDbEvent.SetTaskTags(tagDbState.selectedTagUuids, tagDbState.selectedTagIds))
                    onTagDbEvent(TagDbEvent.UncheckAllTags)
                }
            ) {
                Text(
                    text = stringResource(R.string.save)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onClose() }
            ) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }
        },
        title = {
            Text(
                text = "Add tags"
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Card(
                    modifier = Modifier
                        .heightIn(max = 280.dp)
                        .fillMaxWidth(),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
                    ),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        item {
                            if (!isEmpty) {
                                tagList.forEach { item ->
                                    val isChecked = tagDbState.selectedTagIds.contains(item.id)
                                    Button(
                                        onClick = {
                                            if (isChecked) {
                                                onTagDbEvent(TagDbEvent.UnselectTag(item.id, item.uuid))
                                            } else {
                                                onTagDbEvent(TagDbEvent.SelectTag(item.id, item.uuid))
                                            }
                                        },
                                        shape = RoundedCornerShape(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            contentColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        contentPadding = PaddingValues(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = {
                                                    if (isChecked) {
                                                        onTagDbEvent(TagDbEvent.UnselectTag(item.id, item.uuid))
                                                    } else {
                                                        onTagDbEvent(TagDbEvent.SelectTag(item.id, item.uuid))
                                                    }
                                                }
                                            )

                                            Text(text = item.title)
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "No tags",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(vertical = 64.dp)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Crossfade(
                    targetState = uiState.isAddTagTextFieldOpen,
                    animationSpec = tween(durationMillis = 300),
                    modifier = Modifier.fillMaxWidth()
                ) { targetState ->
                    if (targetState) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            TextField(
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .fillMaxWidth(),
                                value = tagDbState.tagTitle,
                                onValueChange = {
                                    onTagDbEvent(TagDbEvent.SetTagTitle(it))
                                },
                                placeholder = { Text(text = "Tag name") }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        onUiEvent(UiEvent.CloseAddTagTextField)
                                        onTagDbEvent(TagDbEvent.SetTagTitle(""))
                                    }
                                ) {
                                    Text(text = "Cancel")
                                }
                                TextButton(
                                    onClick = {
                                        onTagDbEvent(TagDbEvent.SaveTagForTask)
                                }
                                ) {
                                    Text(
                                        text = stringResource(R.string.save)
                                    )
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { onUiEvent(UiEvent.OpenAddTagTextField) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Rounded.Add, contentDescription = "plus")
                                Text(text = "Add new Tag")
                            }
                        }
                    }
                }
            }
        }
    )
}