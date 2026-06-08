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

    /**
     * Standalone stream of the persisted "is blocking active" flag.
     * Mirrors the value inside [uiState] but is exposed separately so
     * callers that only care about the toggle don't have to subscribe
     * to the heavier dashboard state.
     */
    val isBlockingActive: StateFlow<Boolean> = observeBlockingEnabledUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

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

    fun toggleBlocking() {
        val current = isBlockingActive.value
        viewModelScope.launch { setBlockingEnabledUseCase(!current) }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}
