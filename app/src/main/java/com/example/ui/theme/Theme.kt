package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = HhdBgDark,
    primaryContainer = HhdSurfaceElevated,
    onPrimaryContainer = CyanAccent,
    secondary = PurpleAccent,
    onSecondary = TextMain,
    secondaryContainer = HhdSurfaceElevated,
    onSecondaryContainer = PurpleAccent,
    tertiary = NeonGreen,
    onTertiary = HhdBgDark,
    background = HhdBgDark,
    onBackground = TextMain,
    surface = HhdSurfaceDark,
    onSurface = TextMain,
    surfaceVariant = HhdSurfaceElevated,
    onSurfaceVariant = TextSub,
    outline = HhdBorder,
    outlineVariant = HhdBorderGlow,
    error = DangerRed,
    onError = TextMain
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep high-fidelity Cyberpunk neon aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
