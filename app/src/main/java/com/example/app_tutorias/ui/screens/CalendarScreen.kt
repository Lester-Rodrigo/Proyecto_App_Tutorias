package com.example.app_tutorias.ui.screens

import androidx.compose.runtime.Composable
import com.example.app_tutorias.ui.components.ScreenPlaceholder

@Composable
fun CalendarScreen(
    onBack: () -> Unit
) {
    ScreenPlaceholder(
        title = "Calendario",
        primaryButtonText = "Volver al inicio",
        onPrimaryClick = onBack
    )
}