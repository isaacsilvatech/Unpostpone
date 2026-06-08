package com.unpostpone.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Theme
//  The single entry point for screens. Provides:
//    • MaterialTheme  (colorScheme + typography + shapes)
//    • SemanticColors (success / warning / info) via LocalSemanticColors
//
//  Unpostpone intentionally does NOT use Material You dynamic color:
//  the brand identity is the teal/cream/orange/sage system, and a phone
//  wallpaper should not change the app's personality. If you ever want to
//  offer dynamic color as an opt-in, gate it behind a user setting.
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
    scrim                 = LightScrim,
    inverseSurface        = LightInverseSurface,
    inverseOnSurface      = LightInverseOnSurface,
    inversePrimary        = LightInversePrimary,
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
    scrim                 = DarkScrim,
    inverseSurface        = DarkInverseSurface,
    inverseOnSurface      = DarkInverseOnSurface,
    inversePrimary        = DarkInversePrimary,
    surfaceTint           = DarkSurfaceTint,
)

val LocalSemanticColors = staticCompositionLocalOf<SemanticColors> {
    error("SemanticColors not provided — wrap your content in UnpostponeTheme {}")
}

object UnpostponeTheme {
    val semantic: SemanticColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSemanticColors.current

    val colors
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme
}

@Composable
fun UnpostponeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val semantic    = if (darkTheme) DarkSemanticColors else LightSemanticColors

    CompositionLocalProvider(
        LocalSemanticColors provides semantic
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            shapes      = UnpostponeShapes,
            content     = content,
        )
    }
}
