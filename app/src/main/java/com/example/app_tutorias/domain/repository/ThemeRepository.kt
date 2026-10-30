package com.example.app_tutorias.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val darkModeEnabled: Flow<Boolean>

    suspend fun setDarkMode(enabled: Boolean)
}