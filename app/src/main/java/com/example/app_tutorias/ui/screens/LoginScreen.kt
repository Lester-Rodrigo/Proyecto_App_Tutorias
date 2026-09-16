package com.example.app_tutorias.ui.screens

import androidx.compose.runtime.Composable
import com.example.app_tutorias.ui.components.ScreenPlaceholder

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    ScreenPlaceholder(
        title = "TutorLink",
        primaryButtonText = "Iniciar sesión",
        onPrimaryClick = onLoginSuccess
    )
}