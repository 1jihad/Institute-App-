package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EtaNavy,
    onPrimary = Color.White,
    primaryContainer = EtaNavyLight,
    onPrimaryContainer = EtaNavyDark,
    secondary = EtaRed,
    onSecondary = Color.White,
    secondaryContainer = EtaRedLight,
    onSecondaryContainer = EtaRedDark,
    tertiary = EtaBlue,
    onTertiary = Color.White,
    tertiaryContainer = EtaBlueLight,
    onTertiaryContainer = EtaBlueDark,
    background = BackgroundCream,
    onBackground = Color(0xFF0A1230),
    surface = SurfaceCard,
    onSurface = Color(0xFF0A1230),
    surfaceVariant = Color(0xFFE2E7F4),
    onSurfaceVariant = Color(0xFF434B68)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7A97FF),
    onPrimary = Color(0xFF001050),
    primaryContainer = EtaNavyDark,
    onPrimaryContainer = EtaNavyLight,
    secondary = Color(0xFFFF6B78),
    onSecondary = Color(0xFF4A0008),
    secondaryContainer = EtaRedDark,
    onSecondaryContainer = EtaRedLight,
    tertiary = Color(0xFF64B5F6),
    onTertiary = Color(0xFF002244),
    tertiaryContainer = EtaBlueDark,
    onTertiaryContainer = EtaBlueLight,
    background = DarkBackground,
    onBackground = Color(0xFFF1F4FD),
    surface = DarkSurface,
    onSurface = Color(0xFFF1F4FD),
    surfaceVariant = Color(0xFF222C4D),
    onSurfaceVariant = Color(0xFFC4CCDC)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
