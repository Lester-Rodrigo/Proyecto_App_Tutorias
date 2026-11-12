package com.example.tutorlink.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tutorlink.domain.repository.CatalogRepository
import com.example.tutorlink.domain.repository.FavoritesRepository
import com.example.tutorlink.domain.repository.SearchHistoryRepository
import com.example.tutorlink.domain.repository.SessionRepository
import com.example.tutorlink.domain.repository.AuthRepository

class TutorLinkViewModelFactory(
    private val catalogRepository: CatalogRepository,
    private val sessionRepository: SessionRepository,
    private val favoritesRepository: FavoritesRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val authRepository: AuthRepository
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
                    searchHistoryRepository,
                authRepository = authRepository
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
