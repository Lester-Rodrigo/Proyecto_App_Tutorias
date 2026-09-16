package com.example.app_tutorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.app_tutorias.navigation.TutorLinkNavHost
import com.example.app_tutorias.ui.theme.TutorLinkTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TutorLinkTheme {
                TutorLinkNavHost()
            }
        }
    }
}