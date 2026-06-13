package com.unpostpone.app.presentation.pomodoro.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    val accentColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant
    val titleRes = when (preset) {
        PomodoroPreset.Classic -> R.string.pomodoro_preset_classic_title
        PomodoroPreset.DeepWork -> R.string.pomodoro_preset_deep_title
        PomodoroPreset.Extended -> R.string.pomodoro_preset_extended_title
        else -> R.string.pomodoro_preset_classic_title
    }
    val cycleMinutes = preset.focusMinutes + preset.breakMinutes
    val selectedCd = stringResource(R.string.pomodoro_preset_selected_cd)

    val chipModifier = modifier
        .defaultMinSize(minWidth = Dimens.PresetChipMinWidth)
        .then(
            if (selected) Modifier.semantics { contentDescription = selectedCd }
            else Modifier,
        )

    Surface(
        modifier = chipModifier,
        shape = RoundedCornerShape(MediumRadius),
        color = containerColor,
        contentColor = titleColor,
        tonalElevation = Dimens.SurfaceFlatElevation,
        border = BorderStroke(Dimens.PresetChipBorderWidth, borderColor),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = Dimens.SpacingM,
                vertical = Dimens.SpacingS,
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (selected) {
                    Spacer(Modifier.width(Dimens.SpacingS))
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconS),
                    )
                }
            }
            Text(
                text = stringResource(
                    R.string.pomodoro_preset_subtitle,
                    preset.focusMinutes,
                    preset.breakMinutes,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.size(Dimens.SpacingXS))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingS),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.pomodoro_preset_total, cycleMinutes),
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Medium,
                )
            }
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
