package com.unpostpone.app.presentation.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.core.util.AccessibilityServiceUtils
import com.unpostpone.app.domain.usecase.blockedapp.GetBlockedAppsUseCase
import com.unpostpone.app.domain.usecase.blockedapp.ObserveBlockingEnabledUseCase
import com.unpostpone.app.domain.usecase.blockedapp.SetBlockingEnabledUseCase
import com.unpostpone.app.domain.usecase.goal.GetGoalsUseCase
import com.unpostpone.app.domain.usecase.statistics.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getGoalsUseCase: GetGoalsUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getBlockedAppsUseCase: GetBlockedAppsUseCase,
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
                getBlockedAppsUseCase(),
            ) { goals, stats, isBlocking, blockedApps ->
                Quad(goals, stats, isBlocking, blockedApps.size)
            }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { (goals, stats, isBlocking, protectedCount) ->
                    _uiState.update {
                        it.copy(
                            todayGoals = goals,
                            todayStatistics = stats,
                            isBlockingActive = isBlocking,
                            isAccessibilityServiceEnabled = AccessibilityServiceUtils.isUnpostponeEnabled(context),
                            protectedAppCount = protectedCount,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun onToggleBlocking(wantActive: Boolean) {
        viewModelScope.launch {
            if (!wantActive) {
                setBlockingEnabledUseCase(false)
                return@launch
            }
            if (AccessibilityServiceUtils.isUnpostponeEnabled(context)) {
                setBlockingEnabledUseCase(true)
            } else {
                _uiState.update { it.copy(showAccessibilityPrompt = true) }
            }
        }
    }

    fun refreshAccessibilityState() {
        _uiState.update {
            it.copy(isAccessibilityServiceEnabled = AccessibilityServiceUtils.isUnpostponeEnabled(context))
        }
    }

    fun dismissAccessibilityPrompt() = _uiState.update { it.copy(showAccessibilityPrompt = false) }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
