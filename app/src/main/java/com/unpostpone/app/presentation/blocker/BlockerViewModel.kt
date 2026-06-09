package com.unpostpone.app.presentation.blocker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.core.tempunlock.TemporaryUnlockManager
import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.usecase.goal.GetGoalsUseCase
import com.unpostpone.app.domain.usecase.statistics.IncrementUnlockAttemptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class BlockerUiState(
    val activeGoals: List<Goal> = emptyList(),
)

@HiltViewModel
class BlockerViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val incrementUnlockAttemptUseCase: IncrementUnlockAttemptUseCase,
    private val temporaryUnlockManager: TemporaryUnlockManager,
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

    fun requestTemporaryUnlock(packageName: String, displayName: String) {
        viewModelScope.launch {
            incrementUnlockAttemptUseCase(today)
            temporaryUnlockManager.start(
                packageName = packageName,
                displayName = displayName,
                durationSeconds = TemporaryUnlockManager.DEFAULT_DURATION_SECONDS,
            )
        }
    }
}
