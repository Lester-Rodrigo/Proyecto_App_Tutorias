package com.example.tutorlink.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tutorlink.domain.repository.ThemeRepository

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