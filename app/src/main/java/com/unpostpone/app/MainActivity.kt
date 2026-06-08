package com.unpostpone.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.unpostpone.app.core.locale.LocalAppLocale
import com.unpostpone.app.core.locale.WithAppLocale
import com.unpostpone.app.data.locale.LanguageManagerImpl
import com.unpostpone.app.presentation.navigation.NavGraph
import com.unpostpone.app.ui.theme.UnpostponeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var languageManager: LanguageManagerImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Push the persisted language to AppCompatDelegate BEFORE the
        // first composition. This avoids a flash of the default locale
        // when the user has a saved preference.
        languageManager.applyPersistedToAppCompat()
        enableEdgeToEdge()

        setContent {
            UnpostponeTheme(darkTheme = isSystemInDarkTheme()) {
                WithAppLocale {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
