package com.unpostpone.app.presentation.pomodoro

import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    var pendingPreset by remember { mutableStateOf<PomodoroPreset?>(null) }
    val onPresetClicked: (PomodoroPreset) -> Unit = { preset ->
        if (preset.name != uiState.selectedPreset.name) {
            if (uiState.isSessionActive) {
                pendingPreset = preset
            } else {
                onEvent(PomodoroEvent.PresetSelected(preset))
            }
        }
    }

    val context = LocalContext.current
    if (uiState.inOvertime) {
        LaunchedEffect(Unit) {
            val intent = Intent(context, PomodoroOvertimeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            runCatching { context.startActivity(intent) }
        }
    }

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
            onPresetClicked = onPresetClicked,
            contentPadding = paddingValues,
        )
    }

    pendingPreset?.let { target ->
        val presetLabel = stringResource(
            R.string.pomodoro_preset_short,
            target.focusMinutes,
            target.breakMinutes,
        )
        AlertDialog(
            onDismissRequest = { pendingPreset = null },
            title = {
                Text(
                    text = stringResource(R.string.pomodoro_preset_change_title),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.pomodoro_preset_change_body, presetLabel),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEvent(PomodoroEvent.PresetSelected(target))
                        pendingPreset = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(stringResource(R.string.pomodoro_preset_change_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingPreset = null }) {
                    Text(stringResource(R.string.pomodoro_preset_change_cancel))
                }
            },
        )
    }
}

@Composable
private fun PomodoroContent(
    uiState: PomodoroUiState,
    onEvent: (PomodoroEvent) -> Unit,
    onPresetClicked: (PomodoroPreset) -> Unit,
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
            onPresetClicked = onPresetClicked,
        )

        PomodoroTimerRing(
            progress = uiState.progress,
            centerText = if (uiState.inOvertime) "00:00" else uiState.formattedRemaining,
            eyebrow = eyebrowFor(uiState),
        )

        PomodoroControls(
            timerState = uiState.timerState,
            isSessionActive = uiState.isSessionActive,
            currentSessionType = uiState.currentSessionType,
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
    onPresetClicked: (PomodoroPreset) -> Unit,
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
                onClick = { onPresetClicked(preset) },
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

@Preview(name = "PomodoroScreen — Idle (light)", showBackground = true)
@Composable
private fun PomodoroScreenPreview_Idle() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroContent(
            uiState = PomodoroUiState(),
            onEvent = {},
            onPresetClicked = {},
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
            onPresetClicked = {},
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
            onPresetClicked = {},
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
            onPresetClicked = {},
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
            onPresetClicked = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}
