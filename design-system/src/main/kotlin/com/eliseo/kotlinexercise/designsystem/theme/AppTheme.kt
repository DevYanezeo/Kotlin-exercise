package com.eliseo.kotlinexercise.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors =
    lightColorScheme(
        primary = Color(0xFF1B5E8C),
        onPrimary = Color.White,
        secondary = Color(0xFF2E7D5A),
        onSecondary = Color.White,
        background = Color(0xFFF5F7FA),
        onBackground = Color(0xFF1A1C1E),
        surface = Color.White,
        onSurface = Color(0xFF1A1C1E),
    )

private val DarkColors =
    darkColorScheme(
        primary = Color(0xFF8EC5FF),
        onPrimary = Color(0xFF003258),
        secondary = Color(0xFF7FD4A8),
        onSecondary = Color(0xFF00391F),
        background = Color(0xFF121416),
        onBackground = Color(0xFFE2E2E5),
        surface = Color(0xFF1A1C1E),
        onSurface = Color(0xFFE2E2E5),
    )

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
