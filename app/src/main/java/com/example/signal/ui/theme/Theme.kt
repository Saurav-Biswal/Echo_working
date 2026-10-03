package com.example.signal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EchoLightColors = lightColorScheme(
    primary        = EchoDarkGreen,
    onPrimary      = EchoSurface,

    secondary      = EchoMint,
    onSecondary    = EchoTextPrimary,

    tertiary       = EchoMintBright,
    onTertiary     = EchoTextPrimary,

    background     = EchoBackground,
    onBackground   = EchoTextPrimary,

    surface        = EchoSurface,
    onSurface      = EchoTextPrimary,

    surfaceVariant = EchoSurfaceSoft,
    onSurfaceVariant = EchoTextSecondary,

    outline        = EchoBorder
)

@Composable
fun SignalTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EchoLightColors,
        typography  = SignalTypography,
        content     = content
    )
}