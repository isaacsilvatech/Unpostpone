package com.unpostpone.app.presentation.focusreminder

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unpostpone.app.R
import com.unpostpone.app.ui.theme.Dimens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class FocusMessage(
    @StringRes val textRes: Int,
    val icon: ImageVector,
)

@HiltViewModel
class FocusReminderViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        FocusReminderUiState(message = MESSAGES.random()),
    )
    val state: StateFlow<FocusReminderUiState> = _state.asStateFlow()

    companion object {
        val MESSAGES: List<FocusMessage> = listOf(
            FocusMessage(R.string.focus_reminder_message_1,  Icons.Filled.Shield),
            FocusMessage(R.string.focus_reminder_message_2,  Icons.Filled.WbSunny),
            FocusMessage(R.string.focus_reminder_message_3,  Icons.Filled.Hiking),
            FocusMessage(R.string.focus_reminder_message_4,  Icons.Filled.Architecture),
            FocusMessage(R.string.focus_reminder_message_5,  Icons.Filled.SelfImprovement),
            FocusMessage(R.string.focus_reminder_message_6,  Icons.Filled.CenterFocusStrong),
            FocusMessage(R.string.focus_reminder_message_7,  Icons.Filled.LocalFireDepartment),
            FocusMessage(R.string.focus_reminder_message_8,  Icons.Filled.AutoStories),
            FocusMessage(R.string.focus_reminder_message_9,  Icons.Filled.TipsAndUpdates),
            FocusMessage(R.string.focus_reminder_message_10, Icons.Filled.Bolt),
            FocusMessage(R.string.focus_reminder_message_11, Icons.Filled.RocketLaunch),
            FocusMessage(R.string.focus_reminder_message_12, Icons.Filled.HourglassEmpty),
            FocusMessage(R.string.focus_reminder_message_13, Icons.Filled.TrackChanges),
            FocusMessage(R.string.focus_reminder_message_14, Icons.Filled.Visibility),
            FocusMessage(R.string.focus_reminder_message_15, Icons.Filled.Psychology),
            FocusMessage(R.string.focus_reminder_message_16, Icons.Filled.Star),
            FocusMessage(R.string.focus_reminder_message_17, Icons.Filled.Explore),
            FocusMessage(R.string.focus_reminder_message_18, Icons.Filled.Whatshot),
            FocusMessage(R.string.focus_reminder_message_19, Icons.Filled.EmojiNature),
            FocusMessage(R.string.focus_reminder_message_20, Icons.Filled.Savings),
            FocusMessage(R.string.focus_reminder_message_21, Icons.AutoMirrored.Filled.TrendingUp),
            FocusMessage(R.string.focus_reminder_message_22, Icons.Filled.Lightbulb),
            FocusMessage(R.string.focus_reminder_message_23, Icons.Filled.Speed),
            FocusMessage(R.string.focus_reminder_message_24, Icons.Filled.AutoAwesome),
        )
    }
}

data class FocusReminderUiState(
    val message: FocusMessage,
)

@Composable
fun FocusReminderScreen(
    onContinue: () -> Unit,
    viewModel: FocusReminderViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = Dimens.SpacingXXL, vertical = Dimens.SpacingScreen),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))

            Icon(
                imageVector = state.message.icon,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(Dimens.SpacingXL))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(state.message.textRes),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Dimens.SpacingL))
                Text(
                    text = stringResource(R.string.focus_reminder_take_moment),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.focus_reminder_continue),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
