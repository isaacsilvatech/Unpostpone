package com.unpostpone.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Motion Tokens
//  Calm, intentional, premium. Every duration is short enough to feel
//  responsive and long enough to feel graceful. The M3 "Emphasized" easing
//  is the default — it accelerates sharply and decelerates softly, which
//  reads as "designed" rather than "robotic".
//
//  Three laws of motion for this product:
//    1. Never bounce. No overshoot, no spring-back on UI elements.
//    2. Never flashy. No confetti, no glow, no rotation, no scale > 1.05.
//    3. Always meaningful. Animate to communicate a state change, not to
//       celebrate. Celebration is reserved for goal completion (a single
//       subtle pulse on the success badge) — that's it.
// ════════════════════════════════════════════════════════════════════════════

// ── Easing ─────────────────────────────────────────────────────────────────
//   Emphasized is M3's signature: cubic-bezier(0.2, 0, 0, 1).
//   Standard is the familiar ease-in-out: cubic-bezier(0.4, 0, 0.2, 1).
//   Decelerate is for elements entering the screen.
//   Accelerate is for elements leaving the screen.

val EmphasizedEasing:  Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
val StandardEasing:    Easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
val DecelerateEasing:  Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
val AccelerateEasing:  Easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)

// ── Durations ──────────────────────────────────────────────────────────────
//   Fast      150ms — ripple, hover, color crossfade, chip selection
//   Medium    250ms — default for most state changes, page transitions
//   Slow      400ms — hero elements: focus ring progress, blocker entrance
//   PageIn    300ms — entering a new screen
//   PageOut   200ms — leaving a screen (faster than entering, never the reverse)

object MotionDuration {
    const val Fast       = 150
    const val Medium     = 250
    const val Slow       = 400
    const val PageIn     = 300
    const val PageOut    = 200
    const val GoalPulse  = 600  // the one place we let motion breathe
}

// ── Springs (for drag, swipe, dismiss interactions) ───────────────────────

val GentleSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness    = Spring.StiffnessMediumLow,
)

// ── Page transitions ───────────────────────────────────────────────────────
//   Use these as defaults when defining NavHost composable transitions.
//   Forward: enter from right with fade. Back: mirror.

data class PageTransitions(
    val enterOffset: IntOffset,
    val exitOffset: IntOffset,
)

val DefaultPageEnter = IntOffset(40.dp.value.toInt(), 0)
val DefaultPageExit  = IntOffset(0, 0)

// ── Spacing tokens (companion to Dimens) ──────────────────────────────────

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

// ── Letter-spacing helpers (used in Type.kt via .sp values, but here for
//    ad-hoc usages that need a named token) ───────────────────────────────

val TrackingTight  = (-0.5).sp
val TrackingNormal = 0.sp
val TrackingWide   = 0.5.sp
