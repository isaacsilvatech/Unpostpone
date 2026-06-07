package com.unpostpone.app.presentation.blocker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.usecase.goal.GetGoalsUseCase
import com.unpostpone.app.domain.usecase.statistics.IncrementUnlockAttemptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class BlockerUiState(
    val activeGoals: List<Goal> = emptyList(),
    val isTemporarilyUnlocked: Boolean = false,
    val unlockCountdown: Int = 0
)

@HiltViewModel
class BlockerViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val incrementUnlockAttemptUseCase: IncrementUnlockAttemptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlockerUiState())
    val uiState: StateFlow<BlockerUiState> = _uiState.asStateFlow()

    private val today: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init { loadActiveGoals() }

    private fun loadActiveGoals() {
        viewModelScope.launch {
            getGoalsUseCase(today).collect { goals ->
                _uiState.update { it.copy(activeGoals = goals.filter { g -> !g.isCompleted }) }
            }
        }
    }

    fun requestTemporaryUnlock() {
        viewModelScope.launch {
            incrementUnlockAttemptUseCase(today)
            _uiState.update { it.copy(isTemporarilyUnlocked = true, unlockCountdown = 300) }
            var remaining = 300
            while (remaining > 0) {
                delay(1_000)
                remaining--
                _uiState.update { it.copy(unlockCountdown = remaining) }
            }
            _uiState.update { it.copy(isTemporarilyUnlocked = false, unlockCountdown = 0) }
        }
    }
}
