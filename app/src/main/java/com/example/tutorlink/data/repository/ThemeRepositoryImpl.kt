package com.example.tutorlink.data.repository

import com.example.tutorlink.data.local.datastore.ThemeDataStore
import com.example.tutorlink.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class ThemeRepositoryImpl(
    private val dataStore: ThemeDataStore
) : ThemeRepository {
    override val darkModeEnabled: Flow<Boolean> = dataStore.darkModeEnabled

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.setDarkMode(enabled)
    }
}