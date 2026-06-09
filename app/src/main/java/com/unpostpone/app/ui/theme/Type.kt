package com.unpostpone.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp


private val tightLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private val manropeBase = TextStyle(
    fontFamily = ManropeFontFamily,
    lineHeightStyle = tightLineHeight,
)

// ── Display — onboarding, splash, hero numbers ────────────────────────────

val DisplayLarge = manropeBase.copy(
    fontWeight    = FontWeight.ExtraBold,
    fontSize      = 64.sp,
    lineHeight    = 72.sp,
    letterSpacing = (-1.0).sp,
)

val DisplayMedium = manropeBase.copy(
    fontWeight    = FontWeight.Bold,
    fontSize      = 45.sp,
    lineHeight    = 52.sp,
    letterSpacing = (-0.25).sp,
)

val DisplaySmall = manropeBase.copy(
    fontWeight    = FontWeight.Bold,
    fontSize      = 36.sp,
    lineHeight    = 44.sp,
    letterSpacing = 0.sp,
)

// ── Headline — section titles, card titles, screen titles ─────────────────

val HeadlineLarge = manropeBase.copy(
    fontWeight    = FontWeight.Bold,
    fontSize      = 32.sp,
    lineHeight    = 40.sp,
    letterSpacing = 0.sp,
)

val HeadlineMedium = manropeBase.copy(
    fontWeight    = FontWeight.Bold,
    fontSize      = 28.sp,
    lineHeight    = 36.sp,
    letterSpacing = 0.sp,
)

val HeadlineSmall = manropeBase.copy(
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 26.sp,
    lineHeight    = 34.sp,
    letterSpacing = 0.sp,
)

// ── Title — list-item primary text, dialog titles, button content ─────────

val TitleLarge = manropeBase.copy(
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 22.sp,
    lineHeight    = 28.sp,
    letterSpacing = 0.sp,
)

val TitleMedium = manropeBase.copy(
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 16.sp,
    lineHeight    = 24.sp,
    letterSpacing = 0.15.sp,
)

val TitleSmall = manropeBase.copy(
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 14.sp,
    lineHeight    = 20.sp,
    letterSpacing = 0.1.sp,
)

// ── Body — paragraphs, descriptions, list-item secondary text ─────────────

val BodyLarge = manropeBase.copy(
    fontWeight    = FontWeight.Normal,
    fontSize      = 16.sp,
    lineHeight    = 24.sp,
    letterSpacing = 0.15.sp,
)

val BodyMedium = manropeBase.copy(
    fontWeight    = FontWeight.Normal,
    fontSize      = 14.sp,
    lineHeight    = 20.sp,
    letterSpacing = 0.25.sp,
)

val BodySmall = manropeBase.copy(
    fontWeight    = FontWeight.Normal,
    fontSize      = 12.sp,
    lineHeight    = 16.sp,
    letterSpacing = 0.4.sp,
)

// ── Label — buttons, chips, captions, all-caps where used ─────────────────

val LabelLarge = manropeBase.copy(
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 14.sp,
    lineHeight    = 20.sp,
    letterSpacing = 0.5.sp,
)

val LabelMedium = manropeBase.copy(
    fontWeight    = FontWeight.Medium,
    fontSize      = 12.sp,
    lineHeight    = 16.sp,
    letterSpacing = 1.2.sp,
)

val LabelSmall = manropeBase.copy(
    fontWeight    = FontWeight.Medium,
    fontSize      = 11.sp,
    lineHeight    = 16.sp,
    letterSpacing = 0.5.sp,
)

// ── Number — JetBrains Mono with tabular figures ───────────────────────────
//   Use these for focus time, streaks, statistics, and any other numeric
//   display that benefits from column alignment and a technical feel.

val NumberDisplayLarge = TextStyle(
    fontFamily     = JetBrainsMonoFontFamily,
    fontWeight     = FontWeight.Bold,
    fontSize       = 64.sp,
    lineHeight     = 72.sp,
    letterSpacing  = (-1.5).sp,
    fontFeatureSettings = "tnum, lnum, zero",
    lineHeightStyle    = tightLineHeight,
)

val NumberDisplayMedium = TextStyle(
    fontFamily     = JetBrainsMonoFontFamily,
    fontWeight     = FontWeight.SemiBold,
    fontSize       = 48.sp,
    lineHeight     = 56.sp,
    letterSpacing  = (-0.75).sp,
    fontFeatureSettings = "tnum, lnum, zero",
    lineHeightStyle    = tightLineHeight,
)

val NumberDisplaySmall = TextStyle(
    fontFamily     = JetBrainsMonoFontFamily,
    fontWeight     = FontWeight.Medium,
    fontSize       = 36.sp,
    lineHeight     = 44.sp,
    letterSpacing  = 0.sp,
    fontFeatureSettings = "tnum, lnum, zero",
    lineHeightStyle    = tightLineHeight,
)

/**
 * The single most important number on a screen — used for the dashboard hero
 * focus time. 72sp dominates the layout; tabular figures keep digits aligned
 * as the value ticks up.
 */
val NumberHeadlineXLarge = TextStyle(
    fontFamily     = JetBrainsMonoFontFamily,
    fontWeight     = FontWeight.Bold,
    fontSize       = 72.sp,
    lineHeight     = 80.sp,
    letterSpacing  = (-2).sp,
    fontFeatureSettings = "tnum, lnum, zero",
    lineHeightStyle    = tightLineHeight,
)

val NumberHeadline = TextStyle(
    fontFamily     = JetBrainsMonoFontFamily,
    fontWeight     = FontWeight.SemiBold,
    fontSize       = 24.sp,
    lineHeight     = 32.sp,
    letterSpacing  = 0.sp,
    fontFeatureSettings = "tnum, lnum, zero",
    lineHeightStyle    = tightLineHeight,
)

val NumberTitle = TextStyle(
    fontFamily     = JetBrainsMonoFontFamily,
    fontWeight     = FontWeight.Medium,
    fontSize       = 16.sp,
    lineHeight     = 24.sp,
    letterSpacing  = 0.15.sp,
    fontFeatureSettings = "tnum, lnum, zero",
    lineHeightStyle    = tightLineHeight,
)

val NumberBody = TextStyle(
    fontFamily     = JetBrainsMonoFontFamily,
    fontWeight     = FontWeight.Normal,
    fontSize       = 14.sp,
    lineHeight     = 20.sp,
    letterSpacing  = 0.25.sp,
    fontFeatureSettings = "tnum, lnum, zero",
    lineHeightStyle    = tightLineHeight,
)

// ── Compose M3 Typography ─────────────────────────────────────────────────

val Typography = Typography(
    displayLarge   = DisplayLarge,
    displayMedium  = DisplayMedium,
    displaySmall   = DisplaySmall,
    headlineLarge  = HeadlineLarge,
    headlineMedium = HeadlineMedium,
    headlineSmall  = HeadlineSmall,
    titleLarge     = TitleLarge,
    titleMedium    = TitleMedium,
    titleSmall     = TitleSmall,
    bodyLarge      = BodyLarge,
    bodyMedium     = BodyMedium,
    bodySmall      = BodySmall,
    labelLarge     = LabelLarge,
    labelMedium    = LabelMedium,
    labelSmall     = LabelSmall,
)
