package com.unpostpone.app.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.unpostpone.app.R

// ════════════════════════════════════════════════════════════════════════════
//  Unpostpone — Type System
//  Two families, one purpose:
//    Manrope        — UI, headlines, body, label. Geometric grotesque with
//                     soft humanist terminals. Modern, premium, used by
//                     Linear, Notion, Loom, Vercel.
//    JetBrains Mono — numbers, statistics, time, tabular data. Optimized for
//                     legibility at small sizes; built-in tabular figures
//                     keep focus-time and streak counters perfectly aligned.
//  Both are loaded via Google Fonts (downloadable fonts) so the app stays
//  lean — no bundled .ttf in the APK, no extra storage cost.
// ════════════════════════════════════════════════════════════════════════════

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

private val manrope          = GoogleFont("Manrope")
private val jetBrainsMono    = GoogleFont("JetBrains Mono")

val ManropeFontFamily = FontFamily(
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.ExtraBold),
)

val JetBrainsMonoFontFamily = FontFamily(
    Font(googleFont = jetBrainsMono, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = jetBrainsMono, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = jetBrainsMono, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = jetBrainsMono, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = jetBrainsMono, fontProvider = provider, weight = FontWeight.Bold),
)
