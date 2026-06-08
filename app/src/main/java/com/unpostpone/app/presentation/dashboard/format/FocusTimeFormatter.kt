package com.unpostpone.app.presentation.dashboard.format

/**
 * Pure-Kotlin formatters for the dashboard focus-time display. Kept free of
 * Android / `Context` / `Resources` types so they can be unit-tested on the
 * JVM without Robolectric.
 */
object FocusTimeFormatter {

    /**
     * Format a focus-time value as `"Xh YYm"` when `minutes >= 60`, otherwise
     * as `"Ym"`. Negative inputs are clamped to 0 — the formatter is for a
     * hero KPI, not a general duration, and "−1h" is never a valid state.
     */
    fun formatFocusTime(minutes: Int): String {
        val safe = minutes.coerceAtLeast(0)
        val hours = safe / 60
        val mins = safe % 60
        return if (hours > 0) {
            "${hours}h ${mins}m"
        } else {
            "${mins}m"
        }
    }

    /**
     * Format a focus-time value as a localized `"X min"` label.
     *
     * The actual localization (and pluralization) is the caller's job: pass
     * `getString = { quantity -> context.resources.getQuantityString(...) }`
     * from a Composable. This keeps the formatter platform-free while still
     * allowing real localized strings in the UI.
     *
     * Negative inputs are clamped to 0 (see [formatFocusTime]).
     */
    fun formatMinutesLabel(
        minutes: Int,
        getString: (Int) -> String,
    ): String {
        val safe = minutes.coerceAtLeast(0)
        return getString(safe)
    }
}
