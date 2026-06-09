package com.unpostpone.app.core.tempunlock

object UnlockDuration {
    const val MIN_MINUTES: Int = 5
    const val MAX_MINUTES: Int = 30
    const val DEFAULT_MINUTES: Int = 5
    val OPTIONS_MINUTES: List<Int> = listOf(5, 10, 15, 20, 30)
}
