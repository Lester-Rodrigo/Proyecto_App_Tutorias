package com.example.app_tutorias.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

private val TutorLinkLightColors = lightColorScheme(
    primary = TutorLinkGreenActive,
    onPrimary = Color.White,
    primaryContainer = TutorFavoriteBackground,
    onPrimaryContainer = TutorLinkGreenText,
    secondary = TutorLinkGreenText,
    onSecondary = Color.White,
    background = TutorLinkBackground,
    onBackground = TutorLinkTextPrimary,
    surface = TutorLinkSurface,
    onSurface = TutorLinkTextPrimary,
    surfaceVariant = TutorLinkInputBackground,
    onSurfaceVariant = TutorLinkTextSecondary,
    outline = TutorLinkDivider,
    error = TutorLinkError,
    onError = Color.White,
    errorContainer = TutorLinkErrorContainer,
    onErrorContainer = TutorLinkError
)

private val TutorLinkDarkColors = darkColorScheme(
    primary = TutorLinkGreen,
    onPrimary = TutorLinkTextPrimary,
    primaryContainer = TutorLinkGreenActive,
    onPrimaryContainer = TutorLinkSurface,
    background = androidx.compose.ui.graphics.Color(0xFF101510),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE1E4DE),
    surface = androidx.compose.ui.graphics.Color(0xFF181D18),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE1E4DE),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF273027),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFC1C9BF),
    outline = androidx.compose.ui.graphics.Color(0xFF899388),
    error = androidx.compose.ui.graphics.Color(0xFFFFB4AB),
    errorContainer = androidx.compose.ui.graphics.Color(0xFF93000A)
)

@Composable
fun TutorLinkTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            TutorLinkDarkColors
        } else {
            TutorLinkLightColors
        },
        typography = TutorLinkTypography,
        shapes = TutorLinkShapes,
        content = content
    )
}