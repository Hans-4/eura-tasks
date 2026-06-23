package com.eura.tasks.ui.screens.homeScreenComponents

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.LocalMovies
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eura.tasks.R
import com.eura.tasks.db.lists.ListDbEvent
import com.eura.tasks.db.lists.ListDbState
import com.eura.tasks.ui.UiEvent
import com.eura.tasks.ui.theme.ColorItems
import com.eura.tasks.ui.theme.blue
import com.eura.tasks.ui.theme.green
import com.eura.tasks.ui.theme.pink
import com.eura.tasks.ui.theme.purple
import com.eura.tasks.ui.theme.red
import com.eura.tasks.ui.theme.yellow

data class TypeItems(
    val icon: ImageVector,
    val title: String,
    val identificationTitle: String,
    val color: ColorItems
)

//TODO: Switch to new view model saving system
@Composable
fun AddNewTaskListDialog(
    onUiEvent: (UiEvent) -> Unit,
    onListDbEvent: (ListDbEvent) -> Unit,
    listDbState: ListDbState,
    onClick: () -> Unit,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val systemThemeIndex = if (darkTheme) 1 else 0

    val red = red[systemThemeIndex]
    val yellow = yellow[systemThemeIndex]
    val green = green[systemThemeIndex]
    val blue = blue[systemThemeIndex]
    val purple = purple[systemThemeIndex]
    val pink = pink[systemThemeIndex]

    val colorMap = remember {
        mapOf(
            "RED" to red,
            "YELLOW" to yellow,
            "GREEN" to green,
            "BLUE" to blue,
            "PURPLE" to purple,
            "PINK" to pink
        )
    }

    val typeItemList = listOf(
        TypeItems(
            icon = Icons.Outlined.Notifications,
            title = stringResource(R.string.reminders),
            identificationTitle = "REMINDERS",
            color = purple
        ),
        TypeItems(
            icon = Icons.Outlined.Flight,
            title = stringResource(R.string.travel),
            identificationTitle = "TRAVEL",
            color = blue
        ),
        TypeItems(
            icon = Icons.Outlined.MonetizationOn,
            title = stringResource(R.string.finance),
            identificationTitle = "FINANCE",
            color = green
        ),
        TypeItems(
            icon = Icons.Outlined.ShoppingCart,
            title = stringResource(R.string.shopping),
            identificationTitle = "SHOPPING",
            color = yellow
        ),
        TypeItems(
            icon = Icons.Outlined.Work,
            title = stringResource(R.string.work),
            identificationTitle = "WORK",
            color = blue
        ),
        TypeItems(
            icon = Icons.Outlined.FavoriteBorder,
            title = stringResource(R.string.healt),
            identificationTitle = "HEALTH",
            color = pink
        ),
        TypeItems(
            icon = Icons.Outlined.LocalMovies,
            title = stringResource(R.string.media),
            identificationTitle = "MEDIA",
            color = yellow
        ),
        TypeItems(
            icon = Icons.Outlined.Checklist,
            title = stringResource(R.string.other),
            identificationTitle = "OTHER",
            color = yellow
        ),
    )

    val selectedItem = typeItemList.find { it.identificationTitle == listDbState.listType }

    AlertDialog(
        title =  {Text("Add new list")},
        text = {
            Column{
                TextField(
                    value = listDbState.listTitle,
                    onValueChange = {
                        onListDbEvent(ListDbEvent.SetListTitle(it))
                    },
                    placeholder = {
                        Text(
                            stringResource(R.string.enter_list_name)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.focusRequester(focusRequester)
                )

                Text(
                    text = "List type",
                    modifier = Modifier.padding(top = 16.dp)
                )

                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    typeItemList.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { item ->
                                Box(modifier = Modifier.weight(1f)) {
                                    TypeSelectorCard(
                                        icon = item.icon,
                                        title = item.title,
                                        color = item.color,
                                        isSelected = listDbState.listType == item.identificationTitle,
                                        onSelect = { onListDbEvent(ListDbEvent.SetListType(item.identificationTitle)) }
                                    )
                                }
                            }
                            if (rowItems.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Text(
                    text = "Color",
                    modifier = Modifier.padding(top = 8.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(colorMap.entries.toList()) { entry ->
                        val colorName = entry.key
                        val colorObject = entry.value

                        ColorSelector(
                            primaryColor = colorObject.primary,
                            isSelected = listDbState.listColor == colorName,
                            onSelect = { onListDbEvent(ListDbEvent.SetListColor(colorName)) })
                    }
                }

                Text(
                    text = "Preview",
                    modifier = Modifier.padding(top = 8.dp)
                )

                ListPreview(
                    title = listDbState.listTitle,
                    icon = selectedItem?.icon ?: Icons.Rounded.BugReport,
                    color = colorMap[listDbState.listColor] ?: purple
                )
            }
        },
        onDismissRequest = {onUiEvent(UiEvent.CloseAddTaskListDialog)},
        confirmButton = {
            TextButton(
                onClick = {
                    onListDbEvent(ListDbEvent.SaveList)
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
    val borderSize = 2.dp

    val animatedDotSize by animateDpAsState(
        targetValue = if (isSelected) dotSize else 0.dp,
        animationSpec = tween(durationMillis = 250),
        label = "Dot animation"
    )

    val animatedBorderSize by animateDpAsState(
        targetValue = if (isSelected) borderSize else 0.dp,
        animationSpec = tween(durationMillis = 250),
        label = "Border animation"
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
        Box(
            modifier = Modifier
                .size(size)
                .border(
                    border = BorderStroke(
                        width = animatedBorderSize,
                        brush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun TypeSelectorCard(
    icon: ImageVector,
    title: String,
    color: ColorItems,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Button(
        onClick = { onSelect() },
        contentPadding = PaddingValues(12.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = SolidColor(if (!isSelected) MaterialTheme.colorScheme.outlineVariant else color.primary)
        ),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (!isSelected) MaterialTheme.colorScheme.surface else color.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                color = color.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color.onPrimaryContainer,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title
            )
        }
    }
}

@Composable
fun ListPreview( //TODO: Refactor to string passes
    title: String,
    icon: ImageVector,
    color: ColorItems
) {
    Card(
        border = BorderStroke(
            width = 1.dp,
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Surface(
                color = color.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color.onPrimaryContainer,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(title)
        }
    }
}