package com.unpostpone.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary            = Md3LightPrimary,
    onPrimary          = Md3LightOnPrimary,
    primaryContainer   = Md3LightPrimaryContainer,
    onPrimaryContainer = Md3LightOnPrimaryContainer,

    secondary            = Md3LightSecondary,
    onSecondary          = Md3LightOnSecondary,
    secondaryContainer   = Md3LightSecondaryContainer,
    onSecondaryContainer = Md3LightOnSecondaryContainer,

    tertiary            = Md3LightTertiary,
    onTertiary          = Md3LightOnTertiary,
    tertiaryContainer   = Md3LightTertiaryContainer,
    onTertiaryContainer = Md3LightOnTertiaryContainer,

    background   = Md3LightBackground,
    onBackground = Md3LightOnBackground,
    surface      = Md3LightSurface,
    onSurface    = Md3LightOnSurface,
    surfaceVariant   = Md3LightSurfaceVariant,
    onSurfaceVariant = Md3LightOnSurfaceVariant,

    error             = Md3LightError,
    onError           = Md3LightOnError,
    errorContainer    = Md3LightErrorContainer,
    onErrorContainer  = Md3LightOnErrorContainer,
    outline           = Md3LightOutline,
)

private val DarkColorScheme = darkColorScheme(
    primary            = Md3DarkPrimary,
    onPrimary          = Md3DarkOnPrimary,
    primaryContainer   = Md3DarkPrimaryContainer,
    onPrimaryContainer = Md3DarkOnPrimaryContainer,

    secondary            = Md3DarkSecondary,
    onSecondary          = Md3DarkOnSecondary,
    secondaryContainer   = Md3DarkSecondaryContainer,
    onSecondaryContainer = Md3DarkOnSecondaryContainer,

    tertiary            = Md3DarkTertiary,
    onTertiary          = Md3DarkOnTertiary,
    tertiaryContainer   = Md3DarkTertiaryContainer,
    onTertiaryContainer = Md3DarkOnTertiaryContainer,

    background   = Md3DarkBackground,
    onBackground = Md3DarkOnBackground,
    surface      = Md3DarkSurface,
    onSurface    = Md3DarkOnSurface,
    surfaceVariant   = Md3DarkSurfaceVariant,
    onSurfaceVariant = Md3DarkOnSurfaceVariant,

    error             = Md3DarkError,
    onError           = Md3DarkOnError,
    errorContainer    = Md3DarkErrorContainer,
    onErrorContainer  = Md3DarkOnErrorContainer,
    outline           = Md3DarkOutline,
)

@Composable
fun UnpostponeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography  = Typography,
        content     = content
    )
}