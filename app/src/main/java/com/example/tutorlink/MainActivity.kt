package com.example.tutorlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tutorlink.navigation.TutorLinkNavHost
import com.example.tutorlink.ui.theme.TutorLinkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutorlink.ui.settings.ThemeViewModel
import com.example.tutorlink.ui.settings.ThemeViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as TutorLinkApplication).container
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