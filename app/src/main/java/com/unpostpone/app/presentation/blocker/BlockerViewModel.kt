package com.unpostpone.app.presentation.blocker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.core.tempunlock.TemporaryUnlockManager
import com.unpostpone.app.domain.repository.UnlockDurationPreferences
import com.unpostpone.app.domain.usecase.statistics.IncrementUnlockAttemptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BlockerViewModel @Inject constructor(
    private val incrementUnlockAttemptUseCase: IncrementUnlockAttemptUseCase,
    private val temporaryUnlockManager: TemporaryUnlockManager,
    unlockDurationPreferences: UnlockDurationPreferences,
) : ViewModel() {

    val currentDurationMinutes: StateFlow<Int> = unlockDurationPreferences.current

    private val today: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun requestTemporaryUnlock(packageName: String, displayName: String) {
        val durationSeconds = currentDurationMinutes.value * 60
        viewModelScope.launch {
            incrementUnlockAttemptUseCase(today)
            temporaryUnlockManager.start(
                packageName = packageName,
                displayName = displayName,
                durationSeconds = durationSeconds,
            )
        }
    }
}
