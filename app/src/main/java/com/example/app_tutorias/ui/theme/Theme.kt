package com.example.app_tutorias.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TutorLightColors = lightColorScheme(
    primary = TutorGreen,
    onPrimary = TutorGreenActive,
    primaryContainer = TutorGreen,
    onPrimaryContainer = TutorTextPrimary,
    secondary = TutorGreenText,
    onSecondary = TutorSurface,
    background = TutorBackground,
    onBackground = TutorTextPrimary,
    surface = TutorSurface,
    onSurface = TutorTextPrimary,
    surfaceVariant = TutorInputBackground,
    onSurfaceVariant = TutorTextSecondary,
    outline = TutorDivider,
    error = TutorError,
    onError = TutorSurface,
    errorContainer = TutorErrorContainer,
    onErrorContainer = TutorError
)

@Composable
fun TutorLinkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TutorLightColors,
        typography = TutorTypography,
        shapes = TutorShapes,
        content = content
    )
}