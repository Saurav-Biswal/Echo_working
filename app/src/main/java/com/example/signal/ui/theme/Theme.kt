package com.example.signal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SignalLightColors = lightColorScheme(
    primary = SignalBlue,
    onPrimary = SignalSurface,

    secondary = SignalViolet,
    onSecondary = SignalSurface,

    tertiary = SignalLime,
    onTertiary = SignalTextPrimary,

    background = SignalBackground,
    onBackground = SignalTextPrimary,

    surface = SignalSurface,
    onSurface = SignalTextPrimary,

    surfaceVariant = SignalSurfaceSoft,
    onSurfaceVariant = SignalTextSecondary,

    outline = SignalBorder
)

@Composable
fun SignalTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SignalLightColors,
        typography = SignalTypography,
        content = content
    )
}