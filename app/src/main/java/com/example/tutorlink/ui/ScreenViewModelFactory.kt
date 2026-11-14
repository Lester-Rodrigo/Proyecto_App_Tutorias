package com.example.tutorlink.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tutorlink.domain.repository.SearchHistoryRepository
import com.example.tutorlink.domain.repository.SessionRepository
import com.example.tutorlink.domain.repository.TutorLinkRepository
import com.example.tutorlink.ui.calendar.CalendarViewModel
import com.example.tutorlink.ui.detail.TutorDetailViewModel
import com.example.tutorlink.ui.home.HomeViewModel
import com.example.tutorlink.ui.search.SearchViewModel

class ScreenViewModelFactory(
    private val repository: TutorLinkRepository,
    private val sessionRepository: SessionRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        HomeViewModel::class.java -> HomeViewModel(repository)
        SearchViewModel::class.java -> SearchViewModel(repository, searchHistoryRepository)
        CalendarViewModel::class.java -> CalendarViewModel(repository)
        TutorDetailViewModel::class.java -> TutorDetailViewModel(repository, sessionRepository)
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}
