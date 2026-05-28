package me.hannes.eura_todo.ui.screens.homeScreenComponents

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.hannes.eura_todo.R
import me.hannes.eura_todo.ui.UiEvent
import me.hannes.eura_todo.ui.theme.blue
import me.hannes.eura_todo.ui.theme.green
import me.hannes.eura_todo.ui.theme.pink
import me.hannes.eura_todo.ui.theme.purple
import me.hannes.eura_todo.ui.theme.red
import me.hannes.eura_todo.ui.theme.yellow
import me.hannes.eura_todo.ui.viewModels.SettingsViewModel


@Composable
fun AddNewTaskListDialog(
    onUiEvent: (UiEvent) -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(),
    onClick: () -> Unit,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val systemThemeIndex = if (darkTheme) 1 else 0

    val red = red[systemThemeIndex]
    val yellow = yellow[systemThemeIndex]
    val green = green[systemThemeIndex]
    val blue = blue[systemThemeIndex]
    val purple = purple[systemThemeIndex]
    val pink = pink[systemThemeIndex]

    var newListName by remember { mutableStateOf("") }

    val colorMap = remember {
        mapOf(
            "red" to red,
            "yellow" to yellow,
            "green" to green,
            "blue" to blue,
            "purple" to purple,
            "pink" to pink
        )
    }

    var selectedColor by remember { mutableStateOf("red") }

    AlertDialog(
        title =  {Text("Add new list")},
        text = {
            Column(
            ) {
                TextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.enter_list_name)
                        )
                    },
                    singleLine = true
                )

                LazyRow(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(colorMap.entries.toList()) { entry ->
                        val colorName = entry.key
                        val colorObject = entry.value

                        ColorSelector(
                            primaryColor = colorObject.primary,
                            isSelected = selectedColor == colorName,
                            onSelect = { selectedColor = colorName })
                    }
                }
            }
        },
        onDismissRequest = {onUiEvent(UiEvent.CloseAddTaskListDialog)},
        confirmButton = {
            TextButton(
                onClick = {
                    settingsViewModel.addItem(
                        name = newListName,
                        color = selectedColor
                    )
                    onUiEvent(UiEvent.CloseAddTaskListDialog)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {onUiEvent(UiEvent.CloseAddTaskListDialog)}
            ) {
                Text(
                    stringResource(R.string.cancel)
                )
            }
        },
    )
}

@Composable
fun ColorSelector(
    primaryColor: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val dotSize = 8.dp
    val size = 24.dp

    val animatedDotSize by animateDpAsState(
        targetValue = if (isSelected) dotSize else 0.dp,
        animationSpec = tween(durationMillis = 250),
        label = "DotAnimation"
    )

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = White),
                onClick = { onSelect() },
            )
            .background(primaryColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(animatedDotSize)
                .background(White, CircleShape)
        )
    }
}
