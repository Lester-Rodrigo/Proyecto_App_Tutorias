package com.example.app_tutorias.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_tutorias.data.BookingFormData
import com.example.app_tutorias.data.MockData
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.data.TutoringSession
import com.example.app_tutorias.data.TutoringStatus
import com.example.app_tutorias.data.filterTutors
import com.example.app_tutorias.data.normalized
import com.example.app_tutorias.data.validateBookingForm
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.app_tutorias.domain.repository.CatalogRepository
import com.example.app_tutorias.domain.repository.FavoritesRepository
import com.example.app_tutorias.domain.repository.SearchHistoryRepository
import com.example.app_tutorias.domain.repository.SessionRepository

class TutorLinkViewModel(
    private val catalogRepository: CatalogRepository,
    private val sessionRepository: SessionRepository,
    private val favoritesRepository: FavoritesRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {
    val categories = MockData.categories
    val tutors = MockData.tutors

    private val _sessions = MutableStateFlow(MockData.initialSessions())
    val sessions = _sessions.asStateFlow()

    private val _homeUiState = MutableStateFlow(buildHomeState(_sessions.value))
    val homeUiState = _homeUiState.asStateFlow()

    private val _searchUiState = MutableStateFlow(buildSearchState())
    val searchUiState = _searchUiState.asStateFlow()

    private val _calendarUiState = MutableStateFlow(
        buildCalendarState(
            sessions = _sessions.value,
            filter = SessionFilter.UPCOMING
        )
    )
    val calendarUiState = _calendarUiState.asStateFlow()

    private val _bookingUiState = MutableStateFlow(BookingUiState())
    val bookingUiState = _bookingUiState.asStateFlow()

    fun tutorById(tutorId: String): Tutor? =
        tutors.firstOrNull { it.id == tutorId }

    fun addReservation(session: TutoringSession) {
        _sessions.update { currentSessions ->
            if (currentSessions.any { it.id == session.id }) {
                currentSessions
            } else {
                currentSessions + session
            }
        }
        refreshHome()
        refreshCalendar()
    }

    fun onSearchQueryChanged(query: String) {
        _searchUiState.value = buildSearchState(
            query = query,
            selectedCategoryId = _searchUiState.value.selectedCategoryId
        )
    }

    fun clearSearchQuery() {
        onSearchQueryChanged("")
    }

    fun onSearchCategorySelected(categoryId: String?) {
        val currentCategoryId = _searchUiState.value.selectedCategoryId
        val updatedCategoryId = if (categoryId == currentCategoryId) {
            null
        } else {
            categoryId
        }

        _searchUiState.value = buildSearchState(
            query = _searchUiState.value.query,
            selectedCategoryId = updatedCategoryId
        )
    }

    fun onSessionFilterSelected(filter: SessionFilter) {
        _calendarUiState.value = buildCalendarState(
            sessions = _sessions.value,
            filter = filter
        )
    }

    fun beginBooking(tutorId: String, destinationId: String) {
        if (_bookingUiState.value.destinationId != destinationId) {
            _bookingUiState.value = BookingUiState(
                tutorId = tutorId,
                destinationId = destinationId
            )
        }
    }

    fun onBookingTopicChanged(topic: String) {
        updateBookingState(
            form = _bookingUiState.value.form.copy(topic = topic),
            topicTouched = true
        )
    }

    fun onBookingDateChanged(date: String) {
        updateBookingState(
            form = _bookingUiState.value.form.copy(date = date),
            dateTouched = true
        )
    }

    fun onBookingTimeChanged(time: String) {
        updateBookingState(
            form = _bookingUiState.value.form.copy(time = time),
            timeTouched = true
        )
    }

    fun onBookingNotesChanged(notes: String) {
        updateBookingState(
            form = _bookingUiState.value.form.copy(notes = notes),
            notesTouched = true
        )
    }

    fun submitBooking(tutor: Tutor) {
        val currentState = _bookingUiState.value
        val normalizedForm = currentState.form.normalized()
        val validation = validateBookingForm(normalizedForm)

        if (!validation.isValid || currentState.isSubmitting ||
            currentState.reservationComplete
        ) {
            _bookingUiState.value = currentState.copy(
                validation = validation,
                topicTouched = true,
                dateTouched = true,
                timeTouched = true,
                notesTouched = true
            )
            return
        }

        _bookingUiState.value = currentState.copy(
            form = normalizedForm,
            validation = validation,
            isSubmitting = true
        )

        viewModelScope.launch {
            delay(700)

            val session = TutoringSession(
                id = "session-${tutor.id}-${normalizedForm.date}-${normalizedForm.time}",
                tutorId = tutor.id,
                tutorName = tutor.name,
                subject = normalizedForm.topic,
                date = LocalDate.parse(normalizedForm.date),
                time = LocalTime.parse(normalizedForm.time),
                notes = normalizedForm.notes,
                status = TutoringStatus.UPCOMING
            )

            addReservation(session)
            _bookingUiState.update {
                it.copy(
                    isSubmitting = false,
                    reservationComplete = true
                )
            }
        }
    }

    private fun buildSearchState(
        query: String = "",
        selectedCategoryId: String? = null
    ) = SearchUiState(
        query = query,
        selectedCategoryId = selectedCategoryId,
        categories = categories,
        filteredTutors = filterTutors(tutors, query, selectedCategoryId)
    )

    private fun buildHomeState(sessions: List<TutoringSession>) = HomeUiState(
        categories = categories,
        recommendedTutors = tutors.take(3),
        nextSession = sessions
            .filter { it.status == TutoringStatus.UPCOMING }
            .minWithOrNull(compareBy(TutoringSession::date, TutoringSession::time))
    )

    private fun buildCalendarState(
        sessions: List<TutoringSession>,
        filter: SessionFilter
    ): CalendarUiState {
        val visibleSessions = when (filter) {
            SessionFilter.UPCOMING -> sessions
                .filter { it.status == TutoringStatus.UPCOMING }
                .sortedWith(compareBy(TutoringSession::date, TutoringSession::time))

            SessionFilter.COMPLETED -> sessions
                .filter { it.status == TutoringStatus.COMPLETED }
                .sortedByDescending { it.date }
        }

        return CalendarUiState(
            selectedFilter = filter,
            visibleSessions = visibleSessions
        )
    }

    private fun refreshCalendar() {
        _calendarUiState.value = buildCalendarState(
            sessions = _sessions.value,
            filter = _calendarUiState.value.selectedFilter
        )
    }

    private fun refreshHome() {
        _homeUiState.value = buildHomeState(_sessions.value)
    }

    private fun updateBookingState(
        form: BookingFormData,
        topicTouched: Boolean = _bookingUiState.value.topicTouched,
        dateTouched: Boolean = _bookingUiState.value.dateTouched,
        timeTouched: Boolean = _bookingUiState.value.timeTouched,
        notesTouched: Boolean = _bookingUiState.value.notesTouched
    ) {
        _bookingUiState.update {
            it.copy(
                form = form,
                validation = validateBookingForm(form),
                topicTouched = topicTouched,
                dateTouched = dateTouched,
                timeTouched = timeTouched,
                notesTouched = notesTouched,
                reservationComplete = false
            )
        }
    }
}
