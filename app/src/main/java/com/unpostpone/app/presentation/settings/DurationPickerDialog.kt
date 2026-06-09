package com.unpostpone.app.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unpostpone.app.R
import com.unpostpone.app.core.tempunlock.UnlockDuration
import com.unpostpone.app.ui.theme.Dimens

@Composable
fun DurationPickerDialog(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val current by viewModel.currentDuration.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_unlock_duration_picker_title)) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(UnlockDuration.OPTIONS_MINUTES) { minutes ->
                    DurationRow(
                        minutes = minutes,
                        isSelected = minutes == current,
                        onClick = {
                            viewModel.setDuration(minutes)
                            onDismiss()
                        },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
            }
        },
    )
}

@Composable
private fun DurationRow(
    minutes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.settings_unlock_duration_option, minutes),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface,
        )
        if (isSelected) {
            Spacer(Modifier.size(Dimens.SpacingM))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp).clip(CircleShape),
            )
        }
    }
}
