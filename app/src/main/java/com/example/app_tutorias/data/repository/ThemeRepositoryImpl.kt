package com.example.app_tutorias.data.repository

import com.example.app_tutorias.data.local.datastore.ThemeDataStore
import com.example.app_tutorias.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class ThemeRepositoryImpl(
    private val dataStore: ThemeDataStore
) : ThemeRepository {
    override val darkModeEnabled: Flow<Boolean> = dataStore.darkModeEnabled

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.setDarkMode(enabled)
    }
}