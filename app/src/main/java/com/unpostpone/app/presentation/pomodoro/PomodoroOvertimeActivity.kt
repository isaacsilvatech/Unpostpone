package com.unpostpone.app.presentation.pomodoro

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unpostpone.app.R
import com.unpostpone.app.domain.model.PomodoroSessionType
import com.unpostpone.app.service.pomodoro.PomodoroTimerEngine
import com.unpostpone.app.service.pomodoro.PomodoroTimerService
import com.unpostpone.app.service.pomodoro.formatRemainingMillis
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.Teal
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PomodoroOvertimeActivity : ComponentActivity() {

    @Inject lateinit var engine: PomodoroTimerEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!shouldShowOvertime()) {
            finish()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        val keyguard = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            keyguard?.requestDismissKeyguard(this, null)
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false

        setContent {
            val state by engine.state.collectAsStateWithLifecycle()
            val shouldShow = state.status == PomodoroTimerEngine.Status.RUNNING &&
                state.remainingMillis <= 0L
            LaunchedEffect(shouldShow) {
                if (!shouldShow) finish()
            }
            PomodoroOvertimeScreen(
                state = state,
                onAddMinute = ::sendAddMinute,
                onStop = ::sendStop,
            )
        }
    }

    private fun sendAddMinute() {
        val intent = Intent(this, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_ADD_MINUTE
        }
        startService(intent)
    }

    override fun onResume() {
        super.onResume()
        if (!shouldShowOvertime()) {
            finish()
        }
    }

    private fun shouldShowOvertime(): Boolean {
        val state = engine.state.value
        return state.status == PomodoroTimerEngine.Status.RUNNING && state.remainingMillis <= 0L
    }

    private fun sendStop() {
        val intent = Intent(this, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_STOP
        }
        startService(intent)
        finish()
    }
}

@Composable
private fun PomodoroOvertimeScreen(
    state: PomodoroTimerEngine.State,
    onAddMinute: () -> Unit,
    onStop: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        top = Dimens.SpacingScreen,
                        bottom = Dimens.SpacingHuge,
                        start = Dimens.SpacingXL,
                        end = Dimens.SpacingXL,
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = subtitleFor(state.sessionType),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Dimens.SpacingHuge))
            Text(
                text = formatRemainingMillis(state.remainingMillis),
                color = Color.White,
                fontSize = 112.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Dimens.SpacingHuge))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
            ) {
                Button(
                    onClick = onAddMinute,
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimens.ButtonHeight + 8.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Teal,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.pomodoro_overtime_button_add_minute),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Button(
                    onClick = onStop,
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimens.ButtonHeight + 8.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Teal,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.pomodoro_overtime_button_stop),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun subtitleFor(sessionType: PomodoroSessionType): String {
    val res = when (sessionType) {
        PomodoroSessionType.FOCUS -> R.string.pomodoro_overtime_subtitle_focus
        PomodoroSessionType.BREAK -> R.string.pomodoro_overtime_subtitle_break
    }
    return stringResource(res)
}
