package com.example.app_tutorias.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TutorLinkLightColors = lightColorScheme(
    primary = TutorLinkGreen,
    onPrimary = TutorLinkGreenActive,
    primaryContainer = TutorLinkGreen,
    onPrimaryContainer = TutorLinkTextPrimary,
    secondary =TutorLinkGreenText,
    onSecondary = TutorLinkSurface,
    background = TutorLinkBackground,
    onBackground =TutorLinkTextPrimary,
    surface = TutorLinkSurface,
    onSurface = TutorLinkTextPrimary,
    surfaceVariant = TutorLinkInputBackground,
    onSurfaceVariant = TutorLinkTextSecondary,
    outline = TutorLinkDivider,
    error = TutorLinkError,
    onError = TutorLinkSurface,
    errorContainer = TutorLinkErrorContainer,
    onErrorContainer = TutorLinkError
)

@Composable
fun TutorLinkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TutorLinkLightColors,
        typography = TutorLinkTypography,
        shapes = TutorLinkShapes,
        content = content
    )
}