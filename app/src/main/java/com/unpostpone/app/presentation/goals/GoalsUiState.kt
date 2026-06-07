package com.unpostpone.app.presentation.goals

import com.unpostpone.app.domain.model.Goal

data class GoalsUiState(
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = false,
    val isAddDialogVisible: Boolean = false,
    val error: String? = null
)
