package me.hannes.eura_tasks.ui.screens.homeScreenComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.hannes.eura_tasks.R
import me.hannes.eura_tasks.db.SortType
import me.hannes.eura_tasks.db.DbState
import me.hannes.eura_tasks.db.DbEvent
import me.hannes.eura_tasks.ui.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortItemsSheet(
    onUiEvent: (UiEvent) -> Unit,
    onDbEvent: (DbEvent) -> Unit,
    dbState: DbState
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {onUiEvent(UiEvent.CloseSortItemSheet)},
        dragHandle = null
    ) {
        Column(

        ) {
            SortType.values().forEach { sortType ->
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {onDbEvent(DbEvent.SortTodos(sortType))},
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val iconSize = 20.dp
                        if (dbState.sortType == sortType) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(iconSize)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(iconSize))
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        val sortLabel = when(sortType) {
                            SortType.ID -> stringResource(R.string.recently_added)
                            SortType.TITLE -> stringResource(R.string.title)
                            SortType.DATE -> stringResource(R.string.date)
                        }

                        Text(text = sortLabel)
                    }
                }
            }
        }
    }
}