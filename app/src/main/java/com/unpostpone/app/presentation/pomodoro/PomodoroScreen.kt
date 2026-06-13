package com.unpostpone.app.presentation.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unpostpone.app.R
import com.unpostpone.app.domain.model.PomodoroPreset
import com.unpostpone.app.domain.model.PomodoroSessionType
import com.unpostpone.app.presentation.pomodoro.components.PomodoroControls
import com.unpostpone.app.presentation.pomodoro.components.PomodoroPresetPill
import com.unpostpone.app.presentation.pomodoro.components.PomodoroTimerRing
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.UnpostponeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    navController: NavController,
    viewModel: PomodoroViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val onEvent: (PomodoroEvent) -> Unit = { viewModel.onEvent(it) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.pomodoro_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.pomodoro_back_cd),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { paddingValues ->
        PomodoroContent(
            uiState = uiState,
            onEvent = onEvent,
            contentPadding = paddingValues,
        )
    }

    if (uiState.showSessionCompleteDialog) {
        SessionCompleteDialog(
            sessionType = uiState.currentSessionType,
            completedFocusCount = uiState.completedFocusCount,
            onStartNext = {
                onEvent(PomodoroEvent.DismissCompleteDialog)
                onEvent(PomodoroEvent.Start)
            },
            onDismiss = { onEvent(PomodoroEvent.DismissCompleteDialog) },
        )
    }
}

@Composable
private fun PomodoroContent(
    uiState: PomodoroUiState,
    onEvent: (PomodoroEvent) -> Unit,
    contentPadding: PaddingValues,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(scrollState)
            .padding(horizontal = Dimens.SpacingXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXXL),
    ) {
        Spacer(Modifier.height(Dimens.SpacingM))

        PresetPillsRow(
            uiState = uiState,
            onEvent = onEvent,
        )

        PomodoroTimerRing(
            progress = uiState.progress,
            centerText = uiState.formattedRemaining,
            eyebrow = eyebrowFor(uiState),
        )

        PomodoroControls(
            timerState = uiState.timerState,
            isSessionActive = uiState.isSessionActive,
            onStart = { onEvent(PomodoroEvent.Start) },
            onPause = { onEvent(PomodoroEvent.Pause) },
            onResume = { onEvent(PomodoroEvent.Resume) },
            onReset = { onEvent(PomodoroEvent.Reset) },
            onSkipToNext = { onEvent(PomodoroEvent.SkipToNext) },
            onCancel = { onEvent(PomodoroEvent.Reset) },
        )

        if (uiState.completedFocusCount > 0) {
            Text(
                text = stringResource(
                    R.string.pomodoro_completed_focus_count,
                    uiState.completedFocusCount,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(Dimens.SpacingXL))
    }
}

@Composable
private fun PresetPillsRow(
    uiState: PomodoroUiState,
    onEvent: (PomodoroEvent) -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            Dimens.SpacingM,
            Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        uiState.availablePresets.forEach { preset ->
            PomodoroPresetPill(
                focusMinutes = preset.focusMinutes,
                breakMinutes = preset.breakMinutes,
                selected = preset.name == uiState.selectedPreset.name,
                onClick = { onEvent(PomodoroEvent.PresetSelected(preset)) },
            )
        }
    }
}

@Composable
private fun eyebrowFor(uiState: PomodoroUiState): String {
    val sessionType = uiState.selectedPreset.sessionTypeFor(uiState.plannedDurationMillis)
    val resId = when (sessionType) {
        PomodoroSessionType.FOCUS -> R.string.pomodoro_session_focus
        PomodoroSessionType.BREAK -> R.string.pomodoro_session_break
    }
    return stringResource(resId)
}

@Composable
private fun SessionCompleteDialog(
    sessionType: PomodoroSessionType,
    completedFocusCount: Int,
    onStartNext: () -> Unit,
    onDismiss: () -> Unit,
) {
    val wasFocus = sessionType == PomodoroSessionType.FOCUS
    val bodyRes = if (wasFocus) R.string.pomodoro_complete_focus_body
    else R.string.pomodoro_complete_break_body
    val actionRes = if (wasFocus) R.string.pomodoro_complete_focus_action
    else R.string.pomodoro_complete_break_action

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.pomodoro_complete_title),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(bodyRes),
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (completedFocusCount > 0) {
                    Spacer(Modifier.height(Dimens.SpacingS))
                    Text(
                        text = stringResource(
                            R.string.pomodoro_completed_focus_count,
                            completedFocusCount,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onStartNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(stringResource(actionRes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.pomodoro_complete_dismiss))
            }
        },
    )
}

@Preview(name = "PomodoroScreen — Idle (light)", showBackground = true)
@Composable
private fun PomodoroScreenPreview_Idle() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroContent(
            uiState = PomodoroUiState(),
            onEvent = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Preview(name = "PomodoroScreen — Running (light)", showBackground = true)
@Composable
private fun PomodoroScreenPreview_Running() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroContent(
            uiState = PomodoroUiState(
                timerState = TimerState.Running,
                remainingMillis = 12 * 60_000L + 30_000L,
                plannedDurationMillis = 25 * 60_000L,
            ),
            onEvent = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Preview(name = "PomodoroScreen — Paused (light)", showBackground = true)
@Composable
private fun PomodoroScreenPreview_Paused() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroContent(
            uiState = PomodoroUiState(
                timerState = TimerState.Paused,
                remainingMillis = 12 * 60_000L + 30_000L,
                plannedDurationMillis = 25 * 60_000L,
            ),
            onEvent = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Preview(name = "PomodoroScreen — Finished (light)", showBackground = true)
@Composable
private fun PomodoroScreenPreview_Finished() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroContent(
            uiState = PomodoroUiState(
                timerState = TimerState.Finished,
                remainingMillis = 0L,
                plannedDurationMillis = 25 * 60_000L,
                currentSessionType = PomodoroSessionType.BREAK,
                completedFocusCount = 2,
            ),
            onEvent = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Preview(name = "PomodoroScreen — Dark", showBackground = true)
@Composable
private fun PomodoroScreenPreview_Dark() {
    UnpostponeTheme(darkTheme = true) {
        PomodoroContent(
            uiState = PomodoroUiState(
                timerState = TimerState.Running,
                currentSessionType = PomodoroSessionType.BREAK,
                selectedPreset = PomodoroPreset.DeepWork,
                remainingMillis = 8 * 60_000L,
                plannedDurationMillis = 20 * 60_000L,
                completedFocusCount = 4,
            ),
            onEvent = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}
