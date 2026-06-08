package com.unpostpone.app.ui.theme

import androidx.compose.ui.graphics.Color

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Color System
//  Anchored to the brand mark in docs/icon.svg and docs/paleta-cores.txt.
//  Four anchor colors drive the entire system:
//    #0F4C5C  Deep Teal   — primary, focus, anchor
//    #F7F4EA  Calm Ivory  — background, surface, the "paper" of the app
//    #F4A261  Action      — kinetic moments, primary CTA, clock hands
//    #84A98C  Sage        — growth, success, streak, the leaf
//  No red, no yellow, no harsh contrast — the palette is warm, low-arousal,
//  and deliberately mid-tone. The cream does the heavy lifting; teal is the
//  identity; orange is the spark; sage is the reward.
// ════════════════════════════════════════════════════════════════════════════

// ── Brand anchors (use these in marketing / icon / splash) ────────────────
val BrandTeal      = Color(0xFF0F4C5C)  // icon background, primary
val BrandCream     = Color(0xFFF7F4EA)  // icon clock-arc, background
val BrandAction    = Color(0xFFF4A261)  // icon hands, kinetic accent
val BrandSage      = Color(0xFF84A98C)  // icon leaf, growth / success
val BrandTealSoft  = Color(0xFF2D6A73)  // paleta-cores secondary

// ── Material 3 — Light scheme ─────────────────────────────────────────────
//   Primary:   brand teal as the dominant identity color
//   Secondary: a desaturated mid-teal — UI chrome, less assertive
//   Tertiary:  warm sienna — the orange "action" sits one step removed from
//              primary, so it can carry kinetic moments without competing
//              with the brand
//   Background: the cream — the app should feel like paper, not plastic

val LightPrimary               = Color(0xFF0F4C5C)
val LightOnPrimary             = Color(0xFFFFFFFF)
val LightPrimaryContainer      = Color(0xFFCDE6EC)
val LightOnPrimaryContainer    = Color(0xFF001F26)

val LightSecondary             = Color(0xFF2D6A73)
val LightOnSecondary           = Color(0xFFFFFFFF)
val LightSecondaryContainer    = Color(0xFFCFE6EA)
val LightOnSecondaryContainer  = Color(0xFF041F23)

val LightTertiary              = Color(0xFF8C4F1F)
val LightOnTertiary            = Color(0xFFFFFFFF)
val LightTertiaryContainer     = Color(0xFFFFDCC2)
val LightOnTertiaryContainer   = Color(0xFF2E1500)

val LightError                 = Color(0xFFA8324B)   // desaturated coral, calm not punitive
val LightOnError               = Color(0xFFFFFFFF)
val LightErrorContainer        = Color(0xFFFFDAD9)
val LightOnErrorContainer      = Color(0xFF40000F)

val LightBackground            = Color(0xFFF7F4EA)  // cream — the paper
val LightOnBackground          = Color(0xFF1A1C1B)
val LightSurface               = Color(0xFFFBFAF5)  // slightly lifted cream
val LightOnSurface             = Color(0xFF1A1C1B)
val LightSurfaceVariant        = Color(0xFFDCE5E3)
val LightOnSurfaceVariant      = Color(0xFF3F4948)

val LightOutline               = Color(0xFF6F7978)
val LightOutlineVariant        = Color(0xFFBEC9C7)
val LightScrim                 = Color(0xFF000000)
val LightInverseSurface        = Color(0xFF2D3130)
val LightInverseOnSurface      = Color(0xFFEFF1EE)
val LightInversePrimary        = Color(0xFF84D0DC)
val LightSurfaceTint           = Color(0xFF0F4C5C)

// ── Material 3 — Dark scheme ──────────────────────────────────────────────
//   Dark is not just "inverted". It is intentional: the brand teal moves to
//   a luminous sky-teal that glows on dark surfaces; containers deepen to
//   charcoal-teal; the background is near-black with a teal undertone, not
//   pure black, so the brand is felt even at rest.

val DarkPrimary                = Color(0xFF84D0DC)
val DarkOnPrimary              = Color(0xFF00363F)
val DarkPrimaryContainer       = Color(0xFF004E5A)
val DarkOnPrimaryContainer     = Color(0xFFCDE6EC)

val DarkSecondary              = Color(0xFFB3CACE)
val DarkOnSecondary            = Color(0xFF1B353A)
val DarkSecondaryContainer     = Color(0xFF324B50)
val DarkOnSecondaryContainer   = Color(0xFFCFE6EA)

val DarkTertiary               = Color(0xFFFFB68C)
val DarkOnTertiary             = Color(0xFF4F2500)
val DarkTertiaryContainer      = Color(0xFF6F3A14)
val DarkOnTertiaryContainer    = Color(0xFFFFDCC2)

val DarkError                  = Color(0xFFFFB3B0)
val DarkOnError                = Color(0xFF5F0A19)
val DarkErrorContainer         = Color(0xFF7E1F2C)
val DarkOnErrorContainer       = Color(0xFFFFDAD9)

val DarkBackground             = Color(0xFF0F1413)  // teal-black, matches icon feel
val DarkOnBackground           = Color(0xFFE1E3E0)
val DarkSurface                = Color(0xFF0F1413)
val DarkOnSurface              = Color(0xFFE1E3E0)
val DarkSurfaceVariant         = Color(0xFF3F4948)
val DarkOnSurfaceVariant       = Color(0xFFBEC9C7)

val DarkOutline                = Color(0xFF889392)
val DarkOutlineVariant         = Color(0xFF3F4948)
val DarkScrim                  = Color(0xFF000000)
val DarkInverseSurface         = Color(0xFFE1E3E0)
val DarkInverseOnSurface       = Color(0xFF2D3130)
val DarkInversePrimary         = Color(0xFF0F4C5C)
val DarkSurfaceTint            = Color(0xFF84D0DC)

// ── Semantic extension colors (success / warning / info) ──────────────────
//   M3 only has `error` as a semantic role. For the productivity context
//   Unpostpone also needs success (goal done), warning (gentle nudge), and
//   info (neutral system message). They are NOT in the M3 ColorScheme — they
//   are exposed via LocalSemanticColors so screens can opt in.
//   All three are deliberately desaturated to match the calm personality:
//   no red, no neon green, no school-bus yellow.

data class SemanticColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
)

val LightSemanticColors = SemanticColors(
    success             = Color(0xFF4A6B45),  // deep sage
    onSuccess           = Color(0xFFFFFFFF),
    successContainer    = Color(0xFFD0E5C8),
    onSuccessContainer  = Color(0xFF0E2607),
    warning             = Color(0xFF8C5A1A),  // deep amber, not red
    onWarning           = Color(0xFFFFFFFF),
    warningContainer    = Color(0xFFFFDDB1),
    onWarningContainer  = Color(0xFF2D1700),
    info                = Color(0xFF2C5F70),  // cool teal-blue, distinct from primary
    onInfo              = Color(0xFFFFFFFF),
    infoContainer       = Color(0xFFC2E5F0),
    onInfoContainer     = Color(0xFF001F26),
)

val DarkSemanticColors = SemanticColors(
    success             = Color(0xFF9BC09A),
    onSuccess           = Color(0xFF0E2607),
    successContainer    = Color(0xFF325032),
    onSuccessContainer  = Color(0xFFD0E5C8),
    warning             = Color(0xFFFFB958),
    onWarning           = Color(0xFF422C00),
    warningContainer    = Color(0xFF5E411B),
    onWarningContainer  = Color(0xFFFFDDB1),
    info                = Color(0xFF9ECDE0),
    onInfo              = Color(0xFF00363F),
    infoContainer       = Color(0xFF0E4856),
    onInfoContainer     = Color(0xFFC2E5F0),
)
