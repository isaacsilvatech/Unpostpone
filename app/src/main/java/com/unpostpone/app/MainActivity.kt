package com.unpostpone.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.unpostpone.app.core.locale.WithAppLocale
import com.unpostpone.app.data.locale.LanguageManagerImpl
import com.unpostpone.app.domain.repository.OnboardingPreferences
import com.unpostpone.app.presentation.navigation.NavGraph
import com.unpostpone.app.presentation.navigation.Screen
import com.unpostpone.app.service.accessibility.UnpostponeAccessibilityService
import com.unpostpone.app.ui.theme.UnpostponeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var languageManager: LanguageManagerImpl
    @Inject lateinit var onboardingPreferences: OnboardingPreferences

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageManager.applyPersistedToAppCompat()
        enableEdgeToEdge()

        val startDestination = resolveStartDestination(intent)

        setContent {
            UnpostponeTheme(darkTheme = isSystemInDarkTheme()) {
                WithAppLocale(languageManager = languageManager) {
                    val controller = rememberNavController()
                    navController = controller
                    NavGraph(
                        navController = controller,
                        startDestination = startDestination,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val packageName = intent.getStringExtra(UnpostponeAccessibilityService.EXTRA_BLOCKED_PACKAGE)
        if (!packageName.isNullOrBlank()) {
            // Why: when the accessibility service fires while MainActivity is
            // already on top, singleTop + onNewIntent reuses the existing
            // instance — we have to drive the navController directly because
            // startDestination was already decided in onCreate.
            // Guard against stacking duplicate Blocker entries: every
            // TYPE_WINDOW_STATE_CHANGED in the blocked app re-fires this
            // intent, and each one would push another Blocker route.
            if (navController?.currentDestination?.route != Screen.Blocker.route) {
                navController?.navigate(Screen.Blocker.createRoute(packageName))
            }
        }
    }

    private fun resolveStartDestination(intent: Intent?): String {
        val blockedPackage = intent?.getStringExtra(UnpostponeAccessibilityService.EXTRA_BLOCKED_PACKAGE)
        if (!blockedPackage.isNullOrBlank()) {
            return Screen.Blocker.createRoute(blockedPackage)
        }
        return if (onboardingPreferences.hasCompletedOnboarding()) Screen.Dashboard.route
        else Screen.Onboarding.route
    }
}
