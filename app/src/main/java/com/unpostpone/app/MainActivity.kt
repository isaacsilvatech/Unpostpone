package com.unpostpone.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.unpostpone.app.core.theme.ThemeMode
import com.unpostpone.app.domain.repository.OnboardingPreferences
import com.unpostpone.app.domain.repository.ThemePreferences
import com.unpostpone.app.presentation.navigation.NavGraph
import com.unpostpone.app.presentation.navigation.Screen
import com.unpostpone.app.service.accessibility.UnpostponeAccessibilityService
import com.unpostpone.app.ui.theme.UnpostponeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var themePreferences: ThemePreferences
    @Inject lateinit var onboardingPreferences: OnboardingPreferences

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
        return if (onboardingPreferences.hasCompletedOnboarding()) Screen.Dashboard.route
        else Screen.Onboarding.route
    }

    companion object {
        const val EXTRA_FROM_UNLOCK_NOTIFICATION = "extra_from_unlock_notification"
    }
}
