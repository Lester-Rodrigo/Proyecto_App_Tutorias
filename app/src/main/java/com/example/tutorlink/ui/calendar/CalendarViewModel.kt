package com.example.tutorlink.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorlink.data.TutoringSession
import com.example.tutorlink.data.TutoringStatus
import com.example.tutorlink.domain.repository.TutorLinkRepository
import com.example.tutorlink.ui.CalendarUiState
import com.example.tutorlink.ui.SessionFilter
import com.example.tutorlink.ui.UiLoadState
import com.example.tutorlink.ui.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalendarViewModel(private val repository: TutorLinkRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState(loadState = UiLoadState.LOADING))
    val uiState = _uiState.asStateFlow()
    private var userId: String? = null

    init {
        viewModelScope.launch {
            repository.data.collect { data ->
                if (userId != data.userId) {
                    userId = data.userId
                    _uiState.value = CalendarUiState(loadState = UiLoadState.LOADING)
                }
                refreshState()
            }
        }
    }

    fun onSessionFilterSelected(filter: SessionFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        refreshState()
    }

    private fun refreshState() {
        val data = repository.data.value
        _uiState.update { state ->
            val sessions = data.sessions.map { it.toUiModel() }
            val visible = when (state.selectedFilter) {
                SessionFilter.UPCOMING -> sessions.filter { it.status == TutoringStatus.UPCOMING }
                    .sortedWith(compareBy(TutoringSession::date, TutoringSession::time))
                SessionFilter.COMPLETED -> sessions.filter { it.status == TutoringStatus.COMPLETED }
                    .sortedWith(compareByDescending(TutoringSession::date).thenByDescending { it.time })
            }
            val loadState = UiLoadState.valueOf(data.sessionLoadState.name)
            state.copy(visibleSessions = visible,
                loadState = if (loadState == UiLoadState.CONTENT && visible.isEmpty()) UiLoadState.EMPTY else loadState,
                errorMessage = data.sessionError)
        }
    }

    fun retryLoading() = repository.retryLoading()
}
