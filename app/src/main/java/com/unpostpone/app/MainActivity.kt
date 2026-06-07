package com.unpostpone.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.compose.rememberNavController
import com.unpostpone.app.presentation.navigation.NavGraph
import com.unpostpone.app.ui.theme.UnpostponeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim  = Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.argb(0xe6, 0xF7, 0xF4, 0xEE),
                darkScrim  = android.graphics.Color.argb(0xe6, 0x08, 0x1A, 0x1F),
            )
        )
        setContent {
            val isDark = isSystemInDarkTheme()
            DisposableEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.TRANSPARENT,
                        darkScrim  = Color.TRANSPARENT,
                        detectDarkMode = { isDark }
                    ),
                    navigationBarStyle = if (isDark) {
                        SystemBarStyle.dark(android.graphics.Color.argb(0xe6, 0x08, 0x1A, 0x1F))
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.argb(0xe6, 0xF7, 0xF4, 0xEE),
                            android.graphics.Color.argb(0xe6, 0x08, 0x1A, 0x1F)
                        )
                    }
                )
                onDispose {}
            }
            UnpostponeTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}