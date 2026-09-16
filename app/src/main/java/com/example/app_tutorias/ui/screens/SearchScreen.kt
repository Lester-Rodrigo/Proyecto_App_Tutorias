package com.example.app_tutorias.ui.screens

import androidx.compose.runtime.Composable
import com.example.app_tutorias.ui.components.ScreenPlaceholder

@Composable
fun SearchScreen(
    onBack: () -> Unit
) {
    ScreenPlaceholder(
        title = "Buscar",
        primaryButtonText = "Volver al inicio",
        onPrimaryClick = onBack
    )
}