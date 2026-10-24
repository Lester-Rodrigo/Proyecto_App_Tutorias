package com.example.app_tutorias.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_tutorias.domain.repository.CatalogRepository
import com.example.app_tutorias.domain.repository.FavoritesRepository
import com.example.app_tutorias.domain.repository.SearchHistoryRepository
import com.example.app_tutorias.domain.repository.SessionRepository

class TutorLinkViewModelFactory(
    private val catalogRepository: CatalogRepository,
    private val sessionRepository: SessionRepository,
    private val favoritesRepository: FavoritesRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                TutorLinkViewModel::class.java
            )
        ) {
            return TutorLinkViewModel(
                catalogRepository = catalogRepository,
                sessionRepository = sessionRepository,
                favoritesRepository = favoritesRepository,
                searchHistoryRepository =
                    searchHistoryRepository
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}