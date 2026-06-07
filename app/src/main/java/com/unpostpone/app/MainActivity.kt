package com.unpostpone.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.unpostpone.app.presentation.navigation.NavGraph
import com.unpostpone.app.ui.theme.UnpostponeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnpostponeTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}