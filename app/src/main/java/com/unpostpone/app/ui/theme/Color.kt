package com.unpostpone.app.ui.theme

import androidx.compose.ui.graphics.Color

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Color System (neutral, teal-anchored)
//
//  Three teal brand anchors carry the identity; everything else is neutral
//  (warm grays, no warm cream, no kinetic orange). This replaced the old
//  four-anchor teal/cream/orange/sage system — the auxiliary sage, sienna and
//  semantic success/warning families were folded back into the neutrals.
//
//  Brand anchors (use these in marketing / icon / splash):
//    #0C4D5B  Teal         — identity, primary
//    #1F6778  Teal Light   — secondary, less assertive
//    #083944  Teal Dark    — pressed, on-primary text in dark scheme
//
//  Every Material 3 token the app actually reads is declared explicitly —
//  including the *Container family. The previous scheme left primaryContainer
//  / secondaryContainer / errorContainer unset, which made them fall back to
//  the M3 baseline (purple). That is now fixed below.
// ════════════════════════════════════════════════════════════════════════════

// ── Brand anchors ─────────────────────────────────────────────────────────
val Teal       = Color(0xFF0C4D5B)  // primary
val TealLight  = Color(0xFF1F6778)  // primary light
val TealDark   = Color(0xFF083944)  // primary dark

// ── Material 3 — Light scheme ─────────────────────────────────────────────
//   Primary:    #0C4D5B  — the brand teal
//   Secondary:  #1F6778  — softer teal for chrome that should not compete
//   Tertiary:   neutral gray — keeps the kinetic role off the brand hue
//   Background: neutral off-white — no warm cream
//   Error:      calm red — used only for actual error states
val LightPrimary               = Teal
val LightOnPrimary             = Color(0xFFFFFFFF)
val LightPrimaryContainer      = Color(0xFFCFE8EE)   // light teal tonal step
val LightOnPrimaryContainer    = TealDark
val LightSecondary             = TealLight
val LightOnSecondary           = Color(0xFFFFFFFF)
val LightSecondaryContainer    = Color(0xFFCFE8EE)   // light teal tonal step
val LightOnSecondaryContainer  = TealDark
val LightTertiary              = Color(0xFF525252)   // neutral gray
val LightOnTertiary            = Color(0xFFFFFFFF)
val LightTertiaryContainer     = Color(0xFFE5E5E5)   // neutral gray
val LightOnTertiaryContainer   = Color(0xFF1A1A1A)
val LightError                 = Color(0xFFB91C1C)   // calm red
val LightOnError               = Color(0xFFFFFFFF)
val LightErrorContainer        = Color(0xFFFEE2E2)   // light red
val LightOnErrorContainer      = Color(0xFF7F1D1D)   // dark red
val LightBackground            = Color(0xFFFAFAFA)   // neutral off-white
val LightOnBackground          = Color(0xFF1A1A1A)
val LightSurface               = Color(0xFFFFFFFF)
val LightOnSurface             = Color(0xFF1A1A1A)
val LightSurfaceVariant        = Color(0xFFF0F0F0)
val LightOnSurfaceVariant      = Color(0xFF525252)
val LightOutline               = Color(0xFFD4D4D4)
val LightOutlineVariant        = Color(0xFFE5E5E5)
val LightInverseSurface        = Color(0xFF1A1A1A)
val LightInverseOnSurface      = Color(0xFFF5F5F5)
val LightInversePrimary        = Color(0xFF84D0DC)
val LightScrim                 = Color(0xFF000000)
val LightSurfaceTint           = Teal

// ── Material 3 — Dark scheme ──────────────────────────────────────────────
//   The brand teal brightens to a luminous sky-teal so it still reads as the
//   same family. Backgrounds are pure neutrals (no teal-black tint).
//   Container roles shift: the *Container tokens become mid-teal in dark
//   mode so a teal-tinted card on a near-black background still has contrast.
val DarkPrimary                = Color(0xFF84D0DC)
val DarkOnPrimary              = TealDark
val DarkPrimaryContainer       = TealLight           // mid teal in dark
val DarkOnPrimaryContainer     = Color(0xFFCFE8EE)
val DarkSecondary              = Color(0xFFB3CACE)
val DarkOnSecondary            = Color(0xFF1B353A)
val DarkSecondaryContainer     = TealLight
val DarkOnSecondaryContainer   = Color(0xFFCFE8EE)
val DarkTertiary               = Color(0xFFA3A3A3)   // neutral gray
val DarkOnTertiary             = Color(0xFF1A1A1A)
val DarkTertiaryContainer      = Color(0xFF262626)   // neutral dark gray
val DarkOnTertiaryContainer    = Color(0xFFE5E5E5)
val DarkError                  = Color(0xFFFCA5A5)
val DarkOnError                = Color(0xFF450A0A)
val DarkErrorContainer         = Color(0xFF7F1D1D)
val DarkOnErrorContainer       = Color(0xFFFEE2E2)
val DarkBackground             = Color(0xFF0A0A0A)   // neutral near-black
val DarkOnBackground           = Color(0xFFE5E5E5)
val DarkSurface                = Color(0xFF171717)
val DarkOnSurface              = Color(0xFFE5E5E5)
val DarkSurfaceVariant         = Color(0xFF262626)
val DarkOnSurfaceVariant       = Color(0xFFA3A3A3)
val DarkOutline                = Color(0xFF404040)
val DarkOutlineVariant         = Color(0xFF262626)
val DarkInverseSurface         = Color(0xFFE5E5E5)
val DarkInverseOnSurface       = Color(0xFF1A1A1A)
val DarkInversePrimary         = Teal
val DarkScrim                  = Color(0xFF000000)
val DarkSurfaceTint            = DarkPrimary
