package com.example.tutorlink.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorlink.data.TutoringSession
import com.example.tutorlink.data.TutoringStatus
import com.example.tutorlink.domain.repository.TutorLinkRepository
import com.example.tutorlink.ui.HomeUiState
import com.example.tutorlink.ui.UiLoadState
import com.example.tutorlink.ui.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TutorLinkRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(loadState = UiLoadState.LOADING))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.data.collect { data ->
                _uiState.value = HomeUiState(
                    userName = data.userName,
                    categories = data.subjects.map { it.toUiModel() },
                    recommendedTutors = data.tutors.take(3).map { it.toUiModel(data.subjects) },
                    nextSession = data.sessions.map { it.toUiModel() }
                        .filter { it.status == TutoringStatus.UPCOMING }
                        .minWithOrNull(compareBy(TutoringSession::date, TutoringSession::time)),
                    loadState = UiLoadState.valueOf(data.catalogLoadState.name),
                    errorMessage = data.catalogError ?: data.sessionError
                )
            }
        }
    }

    fun retryLoading() = repository.retryLoading()
}
