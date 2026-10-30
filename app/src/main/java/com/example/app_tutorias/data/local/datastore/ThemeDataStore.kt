package com.example.app_tutorias.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "theme_preferences")
class ThemeDataStore(
    private val context: Context
) {
    val darkModeEnabled: Flow<Boolean> =
        context.themeDataStore.data.map { preferences -> preferences[DARK_MODE_KEY] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { preferences -> preferences[DARK_MODE_KEY] = enabled }
    }

    private companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled") }
}