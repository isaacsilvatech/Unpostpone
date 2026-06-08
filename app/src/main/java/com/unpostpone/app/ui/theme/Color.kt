package com.unpostpone.app.ui.theme

import androidx.compose.ui.graphics.Color

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Color System (80 / 15 / 5)
//
//  The UI is neutral-first. Three teal shades are the only brand hues and
//  they show up sparingly — FAB, primary buttons, selected states, progress,
//  switches. Everything else is white / off-white / hairline gray.
//
//  Distribution target:
//    ~80%  neutrals  — backgrounds, surfaces, text, borders, dividers
//    ~15%  brand     — teal #0C4D5B on FABs, buttons, active progress
//     ~5%  accents   — error red on the Blocker screen only; green is not
//                      a brand color (success is conveyed by the check icon,
//                      not a tinted background)
//
//  `primaryContainer` is intentionally a *very* subtle teal tint (#E8F2F4)
//  so it can be used for the bottom-nav selected indicator without
//  screaming. It is NOT used for card backgrounds — cards are always
//  `surface` (white).
// ════════════════════════════════════════════════════════════════════════════

// ── Brand anchors ─────────────────────────────────────────────────────────
val Teal       = Color(0xFF0C4D5B)  // primary — the only saturated color
val TealLight  = Color(0xFF1F6778)  // primary light — softer teal
val TealDark   = Color(0xFF083944)  // primary dark — pressed, on-primary text
val TealHint   = Color(0xFFE8F2F4)  // primary container — whisper of teal
val TealHintDark = Color(0xFF1F4047) // dark-mode primary container

// ── Material 3 — Light scheme ─────────────────────────────────────────────
val LightBackground            = Color(0xFFFAFAFA)   // page bg
val LightOnBackground          = Color(0xFF0A0A0A)   // primary text
val LightSurface               = Color(0xFFFFFFFF)   // cards
val LightOnSurface             = Color(0xFF0A0A0A)
val LightSurfaceVariant        = Color(0xFFF4F4F4)   // subtle neutral fill
val LightOnSurfaceVariant      = Color(0xFF6B6B6B)   // muted text

// M3 1.4 surface container family — used by TopAppBar, NavigationBar, etc.
val LightSurfaceContainerLowest  = Color(0xFFFFFFFF)
val LightSurfaceContainerLow     = Color(0xFFFAFAFA)
val LightSurfaceContainer        = Color(0xFFFAFAFA)
val LightSurfaceContainerHigh    = Color(0xFFF4F4F4)
val LightSurfaceContainerHighest = Color(0xFFEEEEEE)

val LightOutline               = Color(0xFFD4D4D4)   // visible borders
val LightOutlineVariant        = Color(0xFFEDEDED)   // hairline dividers

val LightPrimary               = Teal
val LightOnPrimary             = Color(0xFFFFFFFF)
val LightPrimaryContainer      = TealHint              // whisper of teal
val LightOnPrimaryContainer    = Teal

val LightSecondary             = TealLight
val LightOnSecondary           = Color(0xFFFFFFFF)
val LightSecondaryContainer    = TealHint
val LightOnSecondaryContainer  = Teal

val LightTertiary              = Color(0xFF525252)   // neutral mid-gray
val LightOnTertiary            = Color(0xFFFFFFFF)
val LightTertiaryContainer     = Color(0xFFF0F0F0)
val LightOnTertiaryContainer   = Color(0xFF1A1A1A)

val LightError                 = Color(0xFFB91C1C)
val LightOnError               = Color(0xFFFFFFFF)
val LightErrorContainer        = Color(0xFFFEE2E2)
val LightOnErrorContainer      = Color(0xFF7F1D1D)

val LightInverseSurface        = Color(0xFF1A1A1A)
val LightInverseOnSurface      = Color(0xFFF5F5F5)
val LightInversePrimary        = Color(0xFF84D0DC)
val LightScrim                 = Color(0xFF000000)
val LightSurfaceTint           = Teal

// ── Material 3 — Dark scheme ──────────────────────────────────────────────
val DarkBackground             = Color(0xFF0A0A0A)
val DarkOnBackground           = Color(0xFFF5F5F5)
val DarkSurface                = Color(0xFF141414)
val DarkOnSurface              = Color(0xFFF5F5F5)
val DarkSurfaceVariant         = Color(0xFF1F1F1F)
val DarkOnSurfaceVariant       = Color(0xFFA3A3A3)

val DarkSurfaceContainerLowest  = Color(0xFF0A0A0A)
val DarkSurfaceContainerLow     = Color(0xFF0A0A0A)
val DarkSurfaceContainer        = Color(0xFF141414)
val DarkSurfaceContainerHigh    = Color(0xFF1F1F1F)
val DarkSurfaceContainerHighest = Color(0xFF262626)

val DarkOutline                = Color(0xFF404040)
val DarkOutlineVariant         = Color(0xFF2E2E2E)

val DarkPrimary                = Color(0xFF5BA3B0)   // lifted teal for dark
val DarkOnPrimary              = TealDark
val DarkPrimaryContainer       = TealHintDark
val DarkOnPrimaryContainer     = Color(0xFFCFE8EE)

val DarkSecondary              = Color(0xFF84A8AE)
val DarkOnSecondary            = Color(0xFF0A2226)
val DarkSecondaryContainer     = TealHintDark
val DarkOnSecondaryContainer   = Color(0xFFCFE8EE)

val DarkTertiary               = Color(0xFFA3A3A3)
val DarkOnTertiary             = Color(0xFF1A1A1A)
val DarkTertiaryContainer      = Color(0xFF262626)
val DarkOnTertiaryContainer    = Color(0xFFE5E5E5)

val DarkError                  = Color(0xFFFCA5A5)
val DarkOnError                = Color(0xFF450A0A)
val DarkErrorContainer         = Color(0xFF5F1A1A)
val DarkOnErrorContainer       = Color(0xFFFECACA)

val DarkInverseSurface         = Color(0xFFF5F5F5)
val DarkInverseOnSurface       = Color(0xFF0A0A0A)
val DarkInversePrimary         = Teal
val DarkScrim                  = Color(0xFF000000)
val DarkSurfaceTint            = DarkPrimary
