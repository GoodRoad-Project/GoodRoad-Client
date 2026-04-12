package com.example.goodroad.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val GoodRoadLightColors = lightColorScheme(
    primary = SafeRoute,
    onPrimary = WhiteSoft,

    secondary = PrimaryBlue,
    onSecondary = WhiteSoft,

    tertiary = InclusiveViolet,
    onTertiary = WhiteSoft,

    error = AlertRed,
    onError = WhiteSoft,

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = SurfaceWarm,
    onSurface = TextPrimary,

    surfaceVariant = BorderWarm,
    onSurfaceVariant = TextSecondary,

    outline = BorderWarm,

    primaryContainer = SafeGreen,
    onPrimaryContainer = TextPrimary,

    secondaryContainer = Water,
    onSecondaryContainer = TextPrimary,

    tertiaryContainer = FastRoute.copy(alpha = 0.18f),
    onTertiaryContainer = TextPrimary,

    errorContainer = Obstacle.copy(alpha = 0.18f),
    onErrorContainer = TextPrimary
)

private val GoodRoadDarkColors = darkColorScheme(
    primary = SafeRoute,
    onPrimary = WhiteSoft,

    secondary = PrimaryBlue,
    onSecondary = WhiteSoft,

    tertiary = InclusiveViolet,
    onTertiary = WhiteSoft,

    error = AlertRed,
    onError = WhiteSoft,

    background = TextPrimary,
    onBackground = BackgroundLight,

    surface = Color(0xFF211D1A),
    onSurface = BackgroundLight,

    surfaceVariant = Color(0xFF4B433D),
    onSurfaceVariant = BorderWarm,

    outline = Color(0xFF6D635A),

    primaryContainer = Color(0xFF355F48),
    onPrimaryContainer = BackgroundLight,

    secondaryContainer = Color(0xFF355D87),
    onSecondaryContainer = BackgroundLight,

    tertiaryContainer = Color(0xFF5D5194),
    onTertiaryContainer = BackgroundLight,

    errorContainer = Color(0xFF8D4A45),
    onErrorContainer = BackgroundLight
)

@Composable
fun GoodRoadTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        GoodRoadDarkColors
    } else {
        GoodRoadLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}