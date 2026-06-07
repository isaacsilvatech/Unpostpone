package com.unpostpone.app.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.usecase.goal.AddGoalUseCase
import com.unpostpone.app.domain.usecase.goal.DeleteGoalUseCase
import com.unpostpone.app.domain.usecase.goal.GetGoalsUseCase
import com.unpostpone.app.domain.usecase.goal.UpdateGoalProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val addGoalUseCase: AddGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase,
    private val updateGoalProgressUseCase: UpdateGoalProgressUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    private val today: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init { loadGoals() }

    private fun loadGoals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getGoalsUseCase(today)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { goals -> _uiState.update { it.copy(goals = goals, isLoading = false) } }
        }
    }

    fun addGoal(name: String, targetMinutes: Int) {
        viewModelScope.launch {
            val goal = Goal(name = name, targetMinutes = targetMinutes, date = today)
            addGoalUseCase(goal).onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            hideAddDialog()
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch { deleteGoalUseCase(goal) }
    }

    fun updateProgress(goal: Goal, newProgress: Int) {
        viewModelScope.launch {
            updateGoalProgressUseCase(goal.id, newProgress, goal.targetMinutes)
        }
    }

    fun showAddDialog() = _uiState.update { it.copy(isAddDialogVisible = true) }
    fun hideAddDialog() = _uiState.update { it.copy(isAddDialogVisible = false) }
    fun dismissError() = _uiState.update { it.copy(error = null) }
}
