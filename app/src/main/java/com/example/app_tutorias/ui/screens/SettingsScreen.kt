package com.example.app_tutorias.ui.screens

import androidx.compose.runtime.Composable
import com.example.app_tutorias.ui.components.ScreenPlaceholder

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    ScreenPlaceholder(
        title = "Configuración",
        primaryButtonText = "Volver al inicio",
        onPrimaryClick = onBack,
        secondaryButtonText = "Cerrar sesión",
        onSecondaryClick = onLogout
    )
}