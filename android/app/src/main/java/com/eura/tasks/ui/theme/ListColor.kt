package com.eura.tasks.ui.theme

import androidx.compose.ui.graphics.Color

data class ColorItems (
    val name: String,
    val type: String,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val surfaceContainer: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
)
val red = listOf(
    ColorItems(
        name = "red",
        type = "light",
        primary = Color(0xFF904B3F),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFDAD4),
        onPrimaryContainer = Color(0xFF73342A),
        surfaceContainer = Color(0xFFFCEAE7),
        onSurface = Color(0xFF231918),
        onSurfaceVariant = Color(0xFF534341)
    ),
    ColorItems(
        name = "red",
        type = "dark",
        primary = Color(0xFFFFB4A7),
        onPrimary = Color(0xFF561E15),
        primaryContainer = Color(0xFF73342A),
        onPrimaryContainer = Color(0xFFFFDAD4),
        surfaceContainer = Color(0xFF271D1C),
        onSurface = Color(0xFFf1dfdb),
        onSurfaceVariant = Color(0xFFD8C2BE)
    ),
)

val yellow = listOf(
    ColorItems(
        name = "yellow",
        type = "light",
        primary = Color(0xFF7A580C),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFDEA7),
        onPrimaryContainer = Color(0xFF5E4200),
        surfaceContainer = Color(0xFFF7ECDF),
        onSurface = Color(0xFF201b13),
        onSurfaceVariant = Color(0xFF4E4639)
    ),
    ColorItems(
        name = "yellow",
        type = "dark",
        primary = Color(0xFFEDC06C),
        onPrimary = Color(0xFF412D00),
        primaryContainer = Color(0xFF5E4200),
        onPrimaryContainer = Color(0xFFFFDEA7),
        surfaceContainer = Color(0xFF241F17),
        onSurface = Color(0xFFECE1D4),
        onSurfaceVariant = Color(0xFFD1C5B4)
    ),
)

val green = listOf(
    ColorItems(
        name = "green",
        type = "light",
        primary = Color(0xFF35693F),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFB6F1BB),
        onPrimaryContainer = Color(0xFF1B5129),
        surfaceContainer = Color(0xFFEBEFE7),
        onSurface = Color(0xFF181D18),
        onSurfaceVariant = Color(0xFF414941)
    ),
    ColorItems(
        name = "green",
        type = "dark",
        primary = Color(0xFF9BD4A0),
        onPrimary = Color(0xFF003915),
        primaryContainer = Color(0xFF1B5129),
        onPrimaryContainer = Color(0xFFB6F1BB),
        surfaceContainer = Color(0xFFEBEFE7),
        onSurface = Color(0xFFE0E4DC),
        onSurfaceVariant = Color(0xFF414941)
    ),
)

val blue = listOf(
    ColorItems(
        name = "blue",
        type = "light",
        primary = Color(0xFF136682),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFBEE9FF),
        onPrimaryContainer = Color(0xFF004D65),
        surfaceContainer = Color(0xFFEAEEF2),
        onSurface = Color(0xFF171C1F),
        onSurfaceVariant = Color(0xFF40484C)
    ),
    ColorItems(
        name = "purple",
        type = "dark",
        primary = Color(0xFF8BD0F0),
        onPrimary = Color(0xFF003546),
        primaryContainer = Color(0xFF004D65),
        onPrimaryContainer = Color(0xFFBEE9FF),
        surfaceContainer = Color(0xFFD0E6F2),
        onSurface = Color(0xFFDFE3E7),
        onSurfaceVariant = Color(0xFFC0C8CD)
    )
)

val purple = listOf(
    ColorItems(
        name = "purple",
        type = "light",
        primary = Color(0xFF595892),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFE2DFFF),
        onPrimaryContainer = Color(0xFF424178),
        surfaceContainer = Color(0xFFf0ecf4),
        onSurface = Color(0xFF1B1B21),
        onSurfaceVariant = Color(0xFF47464F)
    ),
    ColorItems(
        name = "purple",
        type = "dark",
        primary = Color(0xFFC2C1FF),
        onPrimary = Color(0xFF2B2A60),
        primaryContainer = Color(0xFF424178),
        onPrimaryContainer = Color(0xFFE2DFFF),
        surfaceContainer = Color(0xFF201F25),
        onSurface = Color(0xFFE0E4DC),
        onSurfaceVariant = Color(0xFFC8C5D0)
    )
)

val pink = listOf(
    ColorItems(
        name = "pink",
        type = "light",
        primary = Color(0xFF8C4A60),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFD9E2),
        onPrimaryContainer = Color(0xFF703349),
        surfaceContainer = Color(0xFFFAEAED),
        onSurface = Color(0xFF22191B),
        onSurfaceVariant = Color(0xFF514347)
    ),
    ColorItems(
        name = "pink",
        type = "dark",
        primary = Color(0xFFFFB0C8),
        onPrimary = Color(0xFF541D32),
        primaryContainer = Color(0xFF703349),
        onPrimaryContainer = Color(0xFFFFD9E2),
        surfaceContainer = Color(0xFF261D20),
        onSurface = Color(0xFFEFDFE1),
        onSurfaceVariant = Color(0xFFD5C2C6)
    )
)