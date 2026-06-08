package com.unpostpone.app.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Spacing & Layout Tokens
//  4dp grid, doubled. Use these everywhere instead of magic numbers.
//  Card padding = SpacingL (16). Screen gutter = SpacingXL (20). Hero block
//  vertical padding = SpacingHuge (32). The app should feel like it breathes.
// ════════════════════════════════════════════════════════════════════════════

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

    // ── Brand mark / hero illustrations ────────────────────────────────
    val BrandMarkSize     = 96.dp
    val BrandMarkLarge    = 160.dp  // splash, onboarding

    // ── Focus ring (the C/clock shape from the icon) ──────────────────
    val FocusRingSize     = 240.dp
    val FocusRingStroke   = 14.dp
    val FocusRingTrack    = 6.dp

    // ── Hero block tokens (dashboard / statistics 30-day hero) ───────
    //   HeroPadding is the interior of the hero card (32dp — generous so
    //   the ring has room to breathe). The ring dimensions already exist
    //   above as FocusRingSize / FocusRingStroke — kept under both names
    //   so the Blocker screen keeps compiling; new code should use the
    //   HeroRing* aliases.
    val HeroPadding          = 32.dp
    val HeroRingSize         = FocusRingSize   // alias — 240dp
    val HeroRingStroke       = FocusRingStroke // alias — 14dp

    // ── Section rhythm ───────────────────────────────────────────────
    val SectionTitleTopGap   = 24.dp  // gap above a section header
    val StatValueSize        = 36.sp  // token used by the NumberHeadline in stat cards

    // ── Hero card shape (alias of HeroRadius in Shape.kt) ───────────
    val HeroCornerRadius     = 32.dp  // 32dp squircle for the hero band
}
