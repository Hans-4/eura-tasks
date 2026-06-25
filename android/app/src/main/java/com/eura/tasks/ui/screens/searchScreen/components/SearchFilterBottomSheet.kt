package com.eura.tasks.ui.screens.searchScreen.components

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
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eura.tasks.R
import com.eura.tasks.db.SearchEvent
import com.eura.tasks.db.SearchFilter
import com.eura.tasks.db.SearchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterBottomSheet(
    onSearchEvent: (SearchEvent) -> Unit,
    searchState: SearchState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = { onDismiss() },
        dragHandle = null
    ) {
        Column(

        ) {
            SearchFilter.values().forEach { searchFilter ->
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSearchEvent(SearchEvent.SetSearchFilter(searchFilter))  },
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val iconSize = 20.dp
                        if (searchState.searchFilter == searchFilter) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(iconSize)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(iconSize))
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        val filterLabel = when(searchFilter) {
                            SearchFilter.TASK -> stringResource(R.string.tasks)
                            SearchFilter.LIST -> stringResource(R.string.list)
                            SearchFilter.TAG -> stringResource(R.string.tags)
                        }

                        Text(text = filterLabel)
                    }
                }
            }
        }
    }
}