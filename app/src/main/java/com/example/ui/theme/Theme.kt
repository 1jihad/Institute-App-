package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = QuestIndigo,
    onPrimary = Color.White,
    primaryContainer = QuestIndigoLight,
    onPrimaryContainer = QuestIndigoDark,
    secondary = QuestEmerald,
    onSecondary = Color.White,
    secondaryContainer = QuestEmeraldLight,
    onSecondaryContainer = QuestEmeraldDark,
    tertiary = QuestGold,
    onTertiary = Color.White,
    tertiaryContainer = QuestGoldLight,
    onTertiaryContainer = QuestGoldDark,
    background = BackgroundCream,
    onBackground = Color(0xFF0F172A),
    surface = SurfaceCard,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = QuestIndigoDark,
    onPrimaryContainer = QuestIndigoLight,
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = QuestEmeraldDark,
    onSecondaryContainer = QuestEmeraldLight,
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = QuestGoldDark,
    onTertiaryContainer = QuestGoldLight,
    background = DarkBackground,
    onBackground = Color(0xFFF8FAFC),
    surface = DarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1)
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
