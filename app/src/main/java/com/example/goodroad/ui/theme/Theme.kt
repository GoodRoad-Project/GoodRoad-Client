package com.example.goodroad.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = SafeGreen,
    secondary = SafeGreenDark,
    surface = SurfaceWarm,
    onSurface = TextPrimary
)

private val DarkColors = darkColorScheme(
    primary = SafeGreen,
    secondary = SafeGreenDark
)

@Composable
fun GoodRoadTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
