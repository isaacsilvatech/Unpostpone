package com.unpostpone.app.presentation.pomodoro.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.unpostpone.app.R
import com.unpostpone.app.ui.theme.Dimens

@Composable
fun PomodoroPresetPill(
    focusMinutes: Int,
    shortBreakMinutes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                       else MaterialTheme.colorScheme.onSurface
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.outlineVariant
    val pillCd = stringResource(R.string.pomodoro_preset_short, focusMinutes, shortBreakMinutes)

    Surface(
        modifier = modifier
            .defaultMinSize(minWidth = Dimens.PresetPillMinWidth)
            .height(Dimens.PresetPillHeight)
            .semantics { contentDescription = pillCd },
        shape = RoundedCornerShape(50),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(Dimens.PresetChipBorderWidth, borderColor),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.SpacingL),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(
                    R.string.pomodoro_preset_short,
                    focusMinutes,
                    shortBreakMinutes,
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
