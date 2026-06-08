package com.unpostpone.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Theme
//  Single entry point for screens. Provides MaterialTheme (colorScheme +
//  typography + shapes).
//
//  Every M3 token the app reads is declared explicitly. The palette is
//  neutral-first: surface = white, background = off-white, text = near-black,
//  brand = teal #0C4D5B used sparingly on FABs, primary buttons, selected
//  states, progress indicators, and switches.
//
//  Unpostpone intentionally does NOT use Material You dynamic color — the
//  brand identity is the teal/neutral system, and a phone wallpaper should
//  not change the app's personality. Add it as an opt-in setting if ever
//  requested.
// ════════════════════════════════════════════════════════════════════════════

private val LightColors = lightColorScheme(
    background                   = LightBackground,
    onBackground                 = LightOnBackground,
    surface                      = LightSurface,
    onSurface                    = LightOnSurface,
    surfaceVariant               = LightSurfaceVariant,
    onSurfaceVariant             = LightOnSurfaceVariant,
    surfaceContainerLowest       = LightSurfaceContainerLowest,
    surfaceContainerLow          = LightSurfaceContainerLow,
    surfaceContainer             = LightSurfaceContainer,
    surfaceContainerHigh         = LightSurfaceContainerHigh,
    surfaceContainerHighest      = LightSurfaceContainerHighest,
    outline                      = LightOutline,
    outlineVariant               = LightOutlineVariant,
    primary                      = LightPrimary,
    onPrimary                    = LightOnPrimary,
    primaryContainer             = LightPrimaryContainer,
    onPrimaryContainer           = LightOnPrimaryContainer,
    secondary                    = LightSecondary,
    onSecondary                  = LightOnSecondary,
    secondaryContainer           = LightSecondaryContainer,
    onSecondaryContainer         = LightOnSecondaryContainer,
    tertiary                     = LightTertiary,
    onTertiary                   = LightOnTertiary,
    tertiaryContainer            = LightTertiaryContainer,
    onTertiaryContainer          = LightOnTertiaryContainer,
    error                        = LightError,
    onError                      = LightOnError,
    errorContainer               = LightErrorContainer,
    onErrorContainer             = LightOnErrorContainer,
    inverseSurface               = LightInverseSurface,
    inverseOnSurface             = LightInverseOnSurface,
    inversePrimary               = LightInversePrimary,
    scrim                        = LightScrim,
    surfaceTint                  = LightSurfaceTint,
)

private val DarkColors = darkColorScheme(
    background                   = DarkBackground,
    onBackground                 = DarkOnBackground,
    surface                      = DarkSurface,
    onSurface                    = DarkOnSurface,
    surfaceVariant               = DarkSurfaceVariant,
    onSurfaceVariant             = DarkOnSurfaceVariant,
    surfaceContainerLowest       = DarkSurfaceContainerLowest,
    surfaceContainerLow          = DarkSurfaceContainerLow,
    surfaceContainer             = DarkSurfaceContainer,
    surfaceContainerHigh         = DarkSurfaceContainerHigh,
    surfaceContainerHighest      = DarkSurfaceContainerHighest,
    outline                      = DarkOutline,
    outlineVariant               = DarkOutlineVariant,
    primary                      = DarkPrimary,
    onPrimary                    = DarkOnPrimary,
    primaryContainer             = DarkPrimaryContainer,
    onPrimaryContainer           = DarkOnPrimaryContainer,
    secondary                    = DarkSecondary,
    onSecondary                  = DarkOnSecondary,
    secondaryContainer           = DarkSecondaryContainer,
    onSecondaryContainer         = DarkOnSecondaryContainer,
    tertiary                     = DarkTertiary,
    onTertiary                   = DarkOnTertiary,
    tertiaryContainer            = DarkTertiaryContainer,
    onTertiaryContainer          = DarkOnTertiaryContainer,
    error                        = DarkError,
    onError                      = DarkOnError,
    errorContainer               = DarkErrorContainer,
    onErrorContainer             = DarkOnErrorContainer,
    inverseSurface               = DarkInverseSurface,
    inverseOnSurface             = DarkInverseOnSurface,
    inversePrimary               = DarkInversePrimary,
    scrim                        = DarkScrim,
    surfaceTint                  = DarkSurfaceTint,
)

@Composable
fun UnpostponeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = UnpostponeShapes,
        content     = content,
    )
}
