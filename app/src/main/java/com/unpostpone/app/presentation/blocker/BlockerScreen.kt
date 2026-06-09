package com.unpostpone.app.presentation.blocker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unpostpone.app.R
import com.unpostpone.app.core.util.AppLabelResolver
import com.unpostpone.app.core.util.NotificationPermissionHelper
import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.presentation.navigation.Screen
import com.unpostpone.app.service.tempunlock.TemporaryUnlockService

@Composable
fun BlockerScreen(
    packageName: String,
    navController: NavController,
    viewModel: BlockerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentDuration by viewModel.currentDurationMinutes.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { _ -> }

    LaunchedEffect(Unit) {
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        vibrator?.vibrate(
            VibrationEffect.createWaveform(
                longArrayOf(0, 250, 100, 250),
                -1,
            ),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(48.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
                Text(
                    text = stringResource(R.string.blocker_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.blocker_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                )
            }

            if (uiState.activeGoals.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.blocker_pending_goals),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    uiState.activeGoals.take(3).forEach { goal -> BlockerGoalItem(goal) }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        navController.navigate(Screen.FocusReminder.createRoute(packageName)) {
                            popUpTo(Screen.Blocker.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.errorContainer,
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.blocker_back_to_focus))
                }

                OutlinedButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            !NotificationPermissionHelper.isGranted(context)
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        showConfirmDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.blocker_unlock_for, currentDuration))
                }
            }
        }
    }

    if (showConfirmDialog) {
        UnlockConfirmDialog(
            durationMinutes = currentDuration,
            onConfirm = {
                showConfirmDialog = false
                val displayName = AppLabelResolver.resolve(context, packageName)
                val durationSeconds = currentDuration * 60
                viewModel.requestTemporaryUnlock(packageName, displayName)
                startTemporaryUnlockService(context, packageName, displayName, durationSeconds)
                (context as? Activity)?.finish()
            },
            onDismiss = { showConfirmDialog = false },
        )
    }
}

@Composable
private fun UnlockConfirmDialog(
    durationMinutes: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.blocker_unlock_dialog_title)) },
        text = { Text(stringResource(R.string.blocker_unlock_dialog_body, durationMinutes)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

private fun startTemporaryUnlockService(
    context: Context,
    packageName: String,
    displayName: String,
    durationSeconds: Int,
) {
    val intent = Intent(context, TemporaryUnlockService::class.java)
        .setAction(TemporaryUnlockService.ACTION_START)
        .putExtra(TemporaryUnlockService.EXTRA_PACKAGE_NAME, packageName)
        .putExtra(TemporaryUnlockService.EXTRA_DISPLAY_NAME, displayName)
        .putExtra(TemporaryUnlockService.EXTRA_DURATION_SECONDS, durationSeconds)
    ContextCompat.startForegroundService(context, intent)
}

@Composable
private fun BlockerGoalItem(goal: Goal) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(
                goal.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { goal.progressPercent },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onErrorContainer,
                trackColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
            )
            Text(
                "${goal.progressMinutes}/${goal.targetMinutes} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
            )
        }
    }
}
