package com.example.pawpal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5269A6),
    onPrimary = Color.White,

    primaryContainer = Color(0xFFDCE2FF),
    onPrimaryContainer = Color(0xFF10204F),

    secondary = Color(0xFF5B6480),
    onSecondary = Color.White,

    secondaryContainer = Color(0xFFE0E5F8),
    onSecondaryContainer = Color(0xFF181E2F),

    background = Color(0xFFF9F9FF),
    onBackground = Color(0xFF1A1B20),

    surface = Color(0xFFF9F9FF),
    onSurface = Color(0xFF1A1B20),

    surfaceVariant = Color(0xFFE3E4EB),
    onSurfaceVariant = Color(0xFF45464F),

    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB7C4FF),
    onPrimary = Color(0xFF20376F),

    primaryContainer = Color(0xFF394F87),
    onPrimaryContainer = Color(0xFFDCE2FF),

    secondary = Color(0xFFC3C9E0),
    onSecondary = Color(0xFF2D3244),

    secondaryContainer = Color(0xFF444A5C),
    onSecondaryContainer = Color(0xFFE0E5F8),

    background = Color(0xFF111318),
    onBackground = Color(0xFFE3E2E9),

    surface = Color(0xFF111318),
    onSurface = Color(0xFFE3E2E9),

    surfaceVariant = Color(0xFF45464F),
    onSurfaceVariant = Color(0xFFC6C6D0),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun PawPalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme =
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}