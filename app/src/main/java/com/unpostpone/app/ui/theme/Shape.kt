package com.unpostpone.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Shape System
//  The brand mark is a squircle — a rounded square with continuous curvature.
//  That single visual decision drives the shape language: every surface in
//  the app is generously rounded, never sharp, never subtle. We push past
//  the M3 defaults (XS 4, S 8, M 12, L 16, XL 28) to a more squircle-native
//  scale: 8 / 12 / 20 / 28 / 36. The 20dp medium corner is the signature —
//  it appears on every card, dialog, and bottom sheet, and is the single
//  most recognizable shape in the app.
// ════════════════════════════════════════════════════════════════════════════

//   None               → 0dp    — full bleed
//   Extra Small        → 8dp    — chips, badges, small inline elements
//   Small              → 12dp   — text fields, segmented controls
//   Medium             → 20dp   — cards, dialogs, the signature shape
//   Large              → 28dp   — bottom sheets, large cards, hero blocks
//   Extra Large        → 36dp   — onboarding illustrations, splash elements

val ExtraSmallRadius = 8.dp
val SmallRadius      = 12.dp
val MediumRadius     = 20.dp
val LargeRadius      = 28.dp
val ExtraLargeRadius = 36.dp

val UnpostponeShapes = Shapes(
    extraSmall = RoundedCornerShape(ExtraSmallRadius),
    small      = RoundedCornerShape(SmallRadius),
    medium     = RoundedCornerShape(MediumRadius),
    large      = RoundedCornerShape(LargeRadius),
    extraLarge = RoundedCornerShape(ExtraLargeRadius),
)
