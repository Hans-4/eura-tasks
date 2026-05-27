package me.hannes.eura_todo.ui.theme

import androidx.compose.ui.graphics.Color

data class ColorItems (
    val name: String,
    val type: String,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val surfaceContainer: Color,
    val onSurfaceVariant: Color,
)

val green = listOf(
    ColorItems(
        name = "green",
        type = "light",
        primary = Color(0xFF35693F),
        primaryContainer = Color(0xFFB6F1BB),
        onPrimaryContainer = Color(0xFF1B5129),
        surfaceContainer = Color(0xFFEBEFE7),
        onSurfaceVariant = Color(0xFF414941)
    ),
    ColorItems(
        name = "green",
        type = "dark",
        primary = Color(0xFF35693F),
        primaryContainer = Color(0xFFB6F1BB),
        onPrimaryContainer = Color(0xFF1B5129),
        surfaceContainer = Color(0xFFEBEFE7),
        onSurfaceVariant = Color(0xFF414941)
    ),
)

val purple = listOf(
    ColorItems(
        name = "purple",
        type = "light",
        primary = Color(0xFF595892),
        primaryContainer = Color(0xFFE2DFFF),
        onPrimaryContainer = Color(0xFF424178),
        surfaceContainer = Color(0xFFf0ecf4),
        onSurfaceVariant = Color(0xFF47464F)
    ),
    ColorItems(
        name = "purple",
        type = "dark",
        primary = Color(0xFF595892),
        primaryContainer = Color(0xFF424178),
        onPrimaryContainer = Color(0xFFE2DFFF),
        surfaceContainer = Color(0xFF201F25),
        onSurfaceVariant = Color(0xFFC8C5D0)
    )
)

