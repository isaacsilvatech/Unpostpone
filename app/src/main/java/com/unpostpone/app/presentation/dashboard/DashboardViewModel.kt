package com.unpostpone.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val getStatisticsUseCase: GetStatisticsUseCase
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
                getStatisticsUseCase.forDate(today)
            ) { goals, stats ->
                DashboardUiState(
                    todayGoals = goals,
                    todayStatistics = stats,
                    isLoading = false
                )
            }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { state -> _uiState.value = state }
        }
    }

    fun toggleBlocking() = _uiState.update { it.copy(isBlockingActive = !it.isBlockingActive) }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}
