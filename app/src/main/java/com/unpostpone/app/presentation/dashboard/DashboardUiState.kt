package com.unpostpone.app.presentation.dashboard

import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.model.Statistics

data class DashboardUiState(
    val todayGoals: List<Goal> = emptyList(),
    val todayStatistics: Statistics? = null,
    val isBlockingActive: Boolean = false,
    val isAccessibilityServiceEnabled: Boolean = false,
    val showAccessibilityPrompt: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val protectedAppCount: Int = 0,
    val dailyTargetMinutes: Int = 240,
    val streakDays: Int = 0,
) {
    val completedGoalsCount: Int get() = todayGoals.count { it.isCompleted }
    val totalGoalsCount: Int get() = todayGoals.size
    val overallProgress: Float
        get() = if (totalGoalsCount == 0) 0f
                else completedGoalsCount.toFloat() / totalGoalsCount.toFloat()
    val focusedMinutes: Int get() = todayStatistics?.focusedMinutes ?: 0
    val blockCount: Int get() = todayStatistics?.blockCount ?: 0
    val unlockAttempts: Int get() = todayStatistics?.unlockAttempts ?: 0
    val dailyGoalProgressPercent: Float
        get() = if (dailyTargetMinutes <= 0) 0f
                else (focusedMinutes.toFloat() / dailyTargetMinutes.toFloat())
                    .coerceIn(0f, 1f)
}
