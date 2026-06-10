package com.unpostpone.app.presentation.pomodoro.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unpostpone.app.R
import com.unpostpone.app.domain.model.PomodoroPreset
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.MediumRadius
import com.unpostpone.app.ui.theme.UnpostponeTheme

@Composable
fun PomodoroPresetChip(
    preset: PomodoroPreset,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surface
    val titleColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant
    val titleRes = when (preset) {
        PomodoroPreset.Classic -> R.string.pomodoro_preset_classic_title
        PomodoroPreset.DeepWork -> R.string.pomodoro_preset_deep_title
        PomodoroPreset.Extended -> R.string.pomodoro_preset_extended_title
        else -> R.string.pomodoro_preset_classic_title
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(MediumRadius),
        color = containerColor,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, borderColor),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = Dimens.SpacingM, vertical = Dimens.SpacingS),
        ) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(
                    R.string.pomodoro_preset_subtitle,
                    preset.focusMinutes,
                    preset.shortBreakMinutes,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(name = "PomodoroPresetChip — Selected", showBackground = true)
@Composable
private fun PomodoroPresetChipPreview_Selected() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroPresetChip(
            preset = PomodoroPreset.Classic,
            selected = true,
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(name = "PomodoroPresetChip — Unselected", showBackground = true)
@Composable
private fun PomodoroPresetChipPreview_Unselected() {
    UnpostponeTheme(darkTheme = false) {
        PomodoroPresetChip(
            preset = PomodoroPreset.DeepWork,
            selected = false,
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
