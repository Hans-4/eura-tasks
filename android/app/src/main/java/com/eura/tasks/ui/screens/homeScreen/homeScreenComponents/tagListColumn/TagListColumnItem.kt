package com.eura.tasks.ui.screens.homeScreen.homeScreenComponents.tagListColumn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eura.tasks.db.tags.TagsEntity
import com.eura.tasks.R


@Composable
fun TagListColumnItem(
    noTags: Boolean,
    tagList: List<TagsEntity>,

    onTagList: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.tags),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (!noTags) {
            tagList.forEachIndexed { index, item ->
                Button(
                    onClick = { onTagList(item.id) },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = item.title)
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        } else {
            Text(
                text = "No Tags",
                modifier = Modifier
                    .padding(top = 104.dp)
                    .fillMaxWidth()
            )
        }
    }
}