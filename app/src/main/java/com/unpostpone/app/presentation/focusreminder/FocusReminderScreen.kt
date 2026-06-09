package com.unpostpone.app.presentation.focusreminder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

@HiltViewModel
class FocusReminderViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        FocusReminderUiState(messageRes = pickMessage(0)),
    )
    val state: StateFlow<FocusReminderUiState> = _state.asStateFlow()

    private fun pickMessage(seed: Int): Int = MESSAGES[seed % MESSAGES.size]

    companion object {
        private val MESSAGES = listOf(
            R.string.focus_reminder_message_1,
            R.string.focus_reminder_message_2,
            R.string.focus_reminder_message_3,
            R.string.focus_reminder_message_4,
            R.string.focus_reminder_message_5,
        )
    }
}

data class FocusReminderUiState(
    val messageRes: Int,
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

            Image(
                painter = painterResource(R.drawable.ic_app_darckbluegreen),
                contentDescription = null,
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.height(Dimens.SpacingXL))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(state.messageRes),
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
