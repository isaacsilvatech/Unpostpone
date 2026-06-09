package com.unpostpone.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.domain.usecase.blockedapp.ObserveBlockingEnabledUseCase
import com.unpostpone.app.domain.usecase.blockedapp.SetBlockingEnabledUseCase
import com.unpostpone.app.domain.usecase.goal.GetGoalsUseCase
import com.unpostpone.app.domain.usecase.statistics.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val observeBlockingEnabledUseCase: ObserveBlockingEnabledUseCase,
    private val setBlockingEnabledUseCase: SetBlockingEnabledUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val today: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                getGoalsUseCase(today),
                getStatisticsUseCase.forDate(today),
                observeBlockingEnabledUseCase(),
            ) { goals, stats, isBlocking ->
                DashboardUiState(
                    todayGoals = goals,
                    todayStatistics = stats,
                    isBlockingActive = isBlocking,
                    isLoading = false,
                )
            }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { state -> _uiState.value = state }
        }
    }

    /**
     * Flips the persisted blocking flag. Reads the current value straight
     * from [observeBlockingEnabledUseCase] (a hot, replay-1 flow backed by
     * SharedPreferences) instead of a [StateFlow] mirror, so we never read
     * a stale [SharingStarted.WhileSubscribed] cache that no UI is
     * collecting. This is what makes the second tap (off) actually
     * register after the first tap (on) — the mirror previously froze at
     * `false` and forced every toggle to write `true`.
     */
    fun toggleBlocking() {
        viewModelScope.launch {
            val current = observeBlockingEnabledUseCase().first()
            setBlockingEnabledUseCase(!current)
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}
