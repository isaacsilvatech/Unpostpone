package com.unpostpone.app.presentation.dashboard.format


object FocusTimeFormatter {


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
    fun formatMinutesLabel(
        minutes: Int,
        getString: (Int) -> String,
    ): String {
        val safe = minutes.coerceAtLeast(0)
        return getString(safe)
    }
}
