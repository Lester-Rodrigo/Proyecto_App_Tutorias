package com.example.app_tutorias.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_tutorias.domain.repository.ThemeRepository

class ThemeViewModelFactory(
    private val repository: ThemeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                ThemeViewModel::class.java
            )
        ) {
            return ThemeViewModel(repository) as T
        }
        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}