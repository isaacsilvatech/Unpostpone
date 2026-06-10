package com.unpostpone.app.presentation.pomodoro.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.unpostpone.app.R
import com.unpostpone.app.presentation.pomodoro.TimerState
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.UnpostponeTheme

@Composable
fun PomodoroControls(
    timerState: TimerState,
    isSessionActive: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onSkipToNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
        ) {
            when (timerState) {
                TimerState.Idle -> {
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ButtonHeight),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.pomodoro_action_start),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                TimerState.Running -> {
                    Button(
                        onClick = onPause,
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ButtonHeight),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.pomodoro_action_pause),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    OutlinedButton(
                        onClick = onSkipToNext,
                        modifier = Modifier.height(Dimens.ButtonHeight),
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = stringResource(R.string.pomodoro_action_skip),
                            modifier = Modifier.size(Dimens.IconM),
                        )
                    }
                }

                TimerState.Paused -> {
                    Button(
                        onClick = onResume,
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ButtonHeight),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.pomodoro_action_resume),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                TimerState.Finished -> {
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ButtonHeight),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.pomodoro_action_start),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }

        if (isSessionActive) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.height(Dimens.ButtonHeightSmall),
            ) {
                Icon(
                    Icons.Default.Replay,
                    contentDescription = stringResource(R.string.pomodoro_action_reset),
                    modifier = Modifier.size(Dimens.IconS),
                )
                Spacer(Modifier.width(Dimens.SpacingS))
                Text(
                    text = stringResource(R.string.pomodoro_action_reset),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Preview(name = "PomodoroControls — Idle", showBackground = true)
@Composable
private fun PomodoroControlsPreview_Idle() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroControls(
            timerState = TimerState.Idle,
            isSessionActive = false,
            onStart = {},
            onPause = {},
            onResume = {},
            onReset = {},
            onSkipToNext = {},
        )
    }
}

@Preview(name = "PomodoroControls — Running", showBackground = true)
@Composable
private fun PomodoroControlsPreview_Running() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroControls(
            timerState = TimerState.Running,
            isSessionActive = true,
            onStart = {},
            onPause = {},
            onResume = {},
            onReset = {},
            onSkipToNext = {},
        )
    }
}

@Preview(name = "PomodoroControls — Paused", showBackground = true)
@Composable
private fun PomodoroControlsPreview_Paused() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroControls(
            timerState = TimerState.Paused,
            isSessionActive = true,
            onStart = {},
            onPause = {},
            onResume = {},
            onReset = {},
            onSkipToNext = {},
        )
    }
}
