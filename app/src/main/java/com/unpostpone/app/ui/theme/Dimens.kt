package com.unpostpone.app.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


object Dimens {
    // 4dp grid
    val SpacingNone   = 0.dp
    val SpacingXS     = 4.dp
    val SpacingS      = 8.dp
    val SpacingM      = 12.dp
    val SpacingL      = 16.dp
    val SpacingXL     = 20.dp
    val SpacingXXL    = 24.dp
    val SpacingHuge   = 32.dp
    val SpacingGiant  = 40.dp
    val SpacingMax    = 48.dp
    val SpacingScreen = 64.dp

    // ── Screen & container ─────────────────────────────────────────────
    val ScreenGutter      = SpacingXL  // 20 — outer padding of any screen
    val CardPadding       = SpacingL   // 16 — interior of a standard card
    val CardPaddingLarge  = SpacingXXL // 24 — interior of a hero card
    val SectionSpacing    = SpacingHuge // 32 — between major sections on a screen
    val ItemSpacing       = SpacingL   // 16 — between items in a list

    // ── Touch targets (Material guideline is 48dp minimum) ─────────────
    val TouchTargetMin    = 48.dp
    val ButtonHeight      = 52.dp
    val ButtonHeightSmall = 40.dp
    val IconButtonSize    = 48.dp

    // ── Icon sizes ─────────────────────────────────────────────────────
    val IconXS            = 16.dp
    val IconS             = 20.dp
    val IconM             = 24.dp
    val IconL             = 32.dp
    val IconXL            = 48.dp

    // ── Focus ring (the C/clock shape from the icon) ──────────────────
    val FocusRingSize     = 240.dp
    val FocusRingStroke   = 14.dp
    val FocusRingTrack    = 6.dp
    val FocusRingTickRadius = 4.dp

    val HeroPadding          = 32.dp
    val HeroRingSize         = FocusRingSize   // alias — 240dp
    val HeroRingStroke       = FocusRingStroke // alias — 14dp

    // ── Section rhythm ───────────────────────────────────────────────
    val SectionTitleTopGap   = 24.dp  // gap above a section header
    val StatValueSize        = 36.sp  // token used by the NumberHeadline in stat cards

    // ── Hero card shape (alias of HeroRadius in Shape.kt) ───────────
    val HeroCornerRadius     = 32.dp  // 32dp squircle for the hero band

    // ── Pomodoro screen ─────────────────────────────────────────────
    val SessionChipMinWidth  = 120.dp // minimum width so the chip never looks like a pill in the corner
    val PresetChipMinWidth   = 160.dp // minimum width for a FlowRow preset card
    val PresetFlowSpacing    = SpacingL   // 16 — between preset chips in the FlowRow
    val HeroZoneSpacing      = SpacingXXL // 24 — between the four hero zones (chip → ring → controls → link)
    val ControlsLinkSpacing  = SpacingM   // 12 — between the main button and the secondary "Reset/Cancel" text-button
    val HeroBorderWidth      = 1.dp
    val PresetChipBorderWidth = 1.dp
    val SurfaceFlatElevation = 0.dp  // explicit zero-elevation token (idiomatic for `tonalElevation = 0.dp`)
    val PomodoroRingSize     = 320.dp  // enlarged timer ring (was FocusRingSize 240dp) — owns the screen now that the hero card is gone
    val PresetPillHeight     = 40.dp   // compact pill that holds "25/5" / "50/10" / "90/15"
    val PresetPillMinWidth   = 72.dp   // minimum tap target for a single digit-pair
}
