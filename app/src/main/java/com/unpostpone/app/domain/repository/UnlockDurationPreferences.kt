package com.unpostpone.app.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface UnlockDurationPreferences {
    val current: StateFlow<Int>
    fun setDuration(minutes: Int)
}
