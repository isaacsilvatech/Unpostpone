package com.unpostpone.app.presentation.statistics

import com.unpostpone.app.domain.model.Statistics

data class StatisticsUiState(
    val recentStats: List<Statistics> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val totalFocusedMinutes: Int get() = recentStats.sumOf { it.focusedMinutes }
    val totalBlockCount: Int get() = recentStats.sumOf { it.blockCount }
    val totalUnlockAttempts: Int get() = recentStats.sumOf { it.unlockAttempts }
}
