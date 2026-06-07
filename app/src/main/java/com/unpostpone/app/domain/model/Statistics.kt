package com.unpostpone.app.domain.model

data class Statistics(
    val date: String,
    val focusedMinutes: Int = 0,
    val blockCount: Int = 0,
    val unlockAttempts: Int = 0
) {
    val focusedHours: Float get() = focusedMinutes / 60f
}
