package com.example.app_tutorias.ui.screens

import androidx.compose.runtime.Composable
import com.example.app_tutorias.ui.components.ScreenPlaceholder

@Composable
fun SettingsScreen(
    onLogout: () -> Unit
) {
    ScreenPlaceholder(
        title = "Configuración",
        primaryButtonText = "Cerrar sesión",
        onPrimaryClick = onLogout
    )
}