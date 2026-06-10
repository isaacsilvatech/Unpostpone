package com.unpostpone.app.presentation.pomodoro.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The whole footer is a single Column: the main row (primary button + side
    // icon button when relevant) plus a small text-button underneath for the
    // secondary action. Nothing here can overlap the ring above because the
    // ring lives in a sibling Column at the screen level.
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.ControlsLinkSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (timerState) {
            TimerState.Idle -> PrimaryActionOnly(
                label = stringResource(R.string.pomodoro_action_start),
                onClick = onStart,
            )

            TimerState.Running -> RunningActions(
                onPause = onPause,
                onSkipToNext = onSkipToNext,
                onReset = onReset,
            )

            TimerState.Paused -> PausedActions(
                onResume = onResume,
                onReset = onReset,
                onCancel = onCancel,
            )

            TimerState.Finished -> FinishedActions(
                onStartNext = onStart,
            )
        }
    }
}

@Composable
private fun PrimaryActionOnly(
    label: String,
    onClick: () -> Unit,
) {
    PrimaryButton(
        label = label,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun RunningActions(
    onPause: () -> Unit,
    onSkipToNext: () -> Unit,
    onReset: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
    ) {
        PrimaryButton(
            label = stringResource(R.string.pomodoro_action_pause),
            onClick = onPause,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Pause,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.IconM),
                )
            },
            modifier = Modifier.weight(1f),
        )
        FilledIconButton(
            onClick = onSkipToNext,
            modifier = Modifier.size(Dimens.IconButtonSize),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = stringResource(R.string.pomodoro_action_skip),
                modifier = Modifier.size(Dimens.IconM),
            )
        }
    }
    SecondaryTextButton(
        text = stringResource(R.string.pomodoro_action_reset),
        onClick = onReset,
    )
}

@Composable
private fun PausedActions(
    onResume: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
    ) {
        PrimaryButton(
            label = stringResource(R.string.pomodoro_action_resume),
            onClick = onResume,
            icon = {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.IconM),
                )
            },
            modifier = Modifier.weight(1f),
        )
        FilledIconButton(
            onClick = onReset,
            modifier = Modifier.size(Dimens.IconButtonSize),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = stringResource(R.string.pomodoro_action_reset),
                modifier = Modifier.size(Dimens.IconM),
            )
        }
    }
    SecondaryTextButton(
        text = stringResource(R.string.pomodoro_action_cancel),
        onClick = onCancel,
    )
}

@Composable
private fun FinishedActions(
    onStartNext: () -> Unit,
) {
    PrimaryActionOnly(
        label = stringResource(R.string.pomodoro_action_start_next),
        onClick = onStartNext,
    )
}

@Composable
private fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(Dimens.ButtonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(Dimens.SpacingS))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun SecondaryTextButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
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
            onCancel = {},
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
            onCancel = {},
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
            onCancel = {},
        )
    }
}

@Preview(name = "PomodoroControls — Finished", showBackground = true)
@Composable
private fun PomodoroControlsPreview_Finished() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroControls(
            timerState = TimerState.Finished,
            isSessionActive = false,
            onStart = {},
            onPause = {},
            onResume = {},
            onReset = {},
            onSkipToNext = {},
            onCancel = {},
        )
    }
}
