package com.example.app_tutorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.app_tutorias.navigation.TutorLinkNavHost
import com.example.app_tutorias.ui.theme.TutorLinkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_tutorias.ui.settings.ThemeViewModel
import com.example.app_tutorias.ui.settings.ThemeViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as TutoriasApplication).container
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(appContainer.themeRepository)
            )
            val darkModeEnabled by
            themeViewModel.darkModeEnabled
                .collectAsStateWithLifecycle()
            TutorLinkTheme(
                darkTheme = darkModeEnabled
            ) {
                TutorLinkNavHost(
                    appContainer = appContainer,
                    darkModeEnabled = darkModeEnabled,
                    onDarkModeChanged =
                        themeViewModel::setDarkMode
                )
            }
        }
    }
}