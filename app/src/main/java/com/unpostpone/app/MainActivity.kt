package com.unpostpone.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.unpostpone.app.core.theme.ThemeMode
import com.unpostpone.app.domain.repository.OnboardingPreferences
import com.unpostpone.app.domain.repository.ThemePreferences
import com.unpostpone.app.presentation.navigation.NavGraph
import com.unpostpone.app.presentation.navigation.Screen
import com.unpostpone.app.service.accessibility.UnpostponeAccessibilityService
import com.unpostpone.app.service.pomodoro.PomodoroNotificationHelper
import com.unpostpone.app.service.pomodoro.PomodoroRingtonePlayer
import com.unpostpone.app.ui.theme.UnpostponeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var themePreferences: ThemePreferences
    @Inject lateinit var onboardingPreferences: OnboardingPreferences
    @Inject lateinit var ringtonePlayer: PomodoroRingtonePlayer

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = resolveStartDestination(intent)

        setContent {
            val themeMode by themePreferences.current.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val effectiveDarkTheme = when (themeMode) {
                ThemeMode.SystemDefault -> systemDark
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        ringtonePlayer.stop()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }
            UnpostponeTheme(darkTheme = effectiveDarkTheme) {
                val controller = rememberNavController()
                navController = controller
                NavGraph(
                    navController = controller,
                    startDestination = startDestination,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val blockedPackage = intent.getStringExtra(UnpostponeAccessibilityService.EXTRA_BLOCKED_PACKAGE)
        if (!blockedPackage.isNullOrBlank()) {
            val target = if (intent.getBooleanExtra(EXTRA_FROM_UNLOCK_NOTIFICATION, false)) {
                Screen.ReBlock.createRoute(blockedPackage)
            } else {
                Screen.Blocker.createRoute(blockedPackage)
            }
            if (navController?.currentDestination?.route != Screen.ReBlock.route &&
                navController?.currentDestination?.route != Screen.Blocker.route
            ) {
                navController?.navigate(target)
            }
            return
        }
        if (intent.getBooleanExtra(EXTRA_OPEN_POMODORO, false)) {
            if (navController?.currentDestination?.route != Screen.Pomodoro.route) {
                navController?.navigate(Screen.Pomodoro.route)
            }
        }
    }

    private fun resolveStartDestination(intent: Intent?): String {
        val blockedPackage = intent?.getStringExtra(UnpostponeAccessibilityService.EXTRA_BLOCKED_PACKAGE)
        if (!blockedPackage.isNullOrBlank()) {
            return if (intent.getBooleanExtra(EXTRA_FROM_UNLOCK_NOTIFICATION, false)) {
                Screen.ReBlock.createRoute(blockedPackage)
            } else {
                Screen.Blocker.createRoute(blockedPackage)
            }
        }
        if (intent?.getBooleanExtra(EXTRA_OPEN_POMODORO, false) == true) {
            return Screen.Pomodoro.route
        }
        return if (onboardingPreferences.hasCompletedOnboarding()) Screen.Dashboard.route
        else Screen.Onboarding.route
    }

    companion object {
        const val EXTRA_FROM_UNLOCK_NOTIFICATION = "extra_from_unlock_notification"
        const val EXTRA_OPEN_POMODORO = PomodoroNotificationHelper.EXTRA_OPEN_POMODORO
    }
}
