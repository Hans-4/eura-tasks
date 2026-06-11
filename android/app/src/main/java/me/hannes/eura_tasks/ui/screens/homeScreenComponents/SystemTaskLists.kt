package me.hannes.eura_tasks.ui.screens.homeScreenComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.hannes.eura_tasks.ui.theme.ColorItems

@Composable
fun SystemTaskLists(
    count: Int,
    icon: ImageVector,
    title: String,
    progress: Float,
    color: ColorItems,
    onTask: () -> Unit
) {
    Button(
        onClick = { onTask() },
        modifier = Modifier.fillMaxWidth(),
        colors = buttonColors(
            containerColor = color.primaryContainer
        ),
        contentPadding = PaddingValues(16.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = color.primary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color.onPrimary,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(24.dp)
                    )
                }

                Text(
                    "$count",
                    fontSize = 30.sp,
                    fontWeight = Bold,
                    color = color.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                title,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = color.primary,
                trackColor = color.primary.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}