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
//  Every M3 token the app reads is declared explicitly — primary, secondary,
//  tertiary, error, surface/background, and the *Container family. The
//  previous version omitted primaryContainer / secondaryContainer /
//  errorContainer, which made those roles fall back to the M3 baseline
//  (purple). All roles are now bound to the teal/neutral palette.
//
//  Unpostpone intentionally does NOT use Material You dynamic color — the
//  brand identity is the teal/neutral system, and a phone wallpaper should
//  not change the app's personality. Add it as an opt-in setting if ever
//  requested.
// ════════════════════════════════════════════════════════════════════════════

private val LightColors = lightColorScheme(
    primary               = LightPrimary,
    onPrimary             = LightOnPrimary,
    primaryContainer      = LightPrimaryContainer,
    onPrimaryContainer    = LightOnPrimaryContainer,
    secondary             = LightSecondary,
    onSecondary           = LightOnSecondary,
    secondaryContainer    = LightSecondaryContainer,
    onSecondaryContainer  = LightOnSecondaryContainer,
    tertiary              = LightTertiary,
    onTertiary            = LightOnTertiary,
    tertiaryContainer     = LightTertiaryContainer,
    onTertiaryContainer   = LightOnTertiaryContainer,
    error                 = LightError,
    onError               = LightOnError,
    errorContainer        = LightErrorContainer,
    onErrorContainer      = LightOnErrorContainer,
    background            = LightBackground,
    onBackground          = LightOnBackground,
    surface               = LightSurface,
    onSurface             = LightOnSurface,
    surfaceVariant        = LightSurfaceVariant,
    onSurfaceVariant      = LightOnSurfaceVariant,
    outline               = LightOutline,
    outlineVariant        = LightOutlineVariant,
    inverseSurface        = LightInverseSurface,
    inverseOnSurface      = LightInverseOnSurface,
    inversePrimary        = LightInversePrimary,
    scrim                 = LightScrim,
    surfaceTint           = LightSurfaceTint,
)

private val DarkColors = darkColorScheme(
    primary               = DarkPrimary,
    onPrimary             = DarkOnPrimary,
    primaryContainer      = DarkPrimaryContainer,
    onPrimaryContainer    = DarkOnPrimaryContainer,
    secondary             = DarkSecondary,
    onSecondary           = DarkOnSecondary,
    secondaryContainer    = DarkSecondaryContainer,
    onSecondaryContainer  = DarkOnSecondaryContainer,
    tertiary              = DarkTertiary,
    onTertiary            = DarkOnTertiary,
    tertiaryContainer     = DarkTertiaryContainer,
    onTertiaryContainer   = DarkOnTertiaryContainer,
    error                 = DarkError,
    onError               = DarkOnError,
    errorContainer        = DarkErrorContainer,
    onErrorContainer      = DarkOnErrorContainer,
    background            = DarkBackground,
    onBackground          = DarkOnBackground,
    surface               = DarkSurface,
    onSurface             = DarkOnSurface,
    surfaceVariant        = DarkSurfaceVariant,
    onSurfaceVariant      = DarkOnSurfaceVariant,
    outline               = DarkOutline,
    outlineVariant        = DarkOutlineVariant,
    inverseSurface        = DarkInverseSurface,
    inverseOnSurface      = DarkInverseOnSurface,
    inversePrimary        = DarkInversePrimary,
    scrim                 = DarkScrim,
    surfaceTint           = DarkSurfaceTint,
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
