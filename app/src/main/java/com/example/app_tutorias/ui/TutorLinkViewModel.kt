package com.example.app_tutorias.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_tutorias.data.BookingFormData
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.data.TutoringSession
import com.example.app_tutorias.data.TutoringStatus
import com.example.app_tutorias.data.filterTutors
import com.example.app_tutorias.data.normalized
import com.example.app_tutorias.data.validateBookingForm
import com.example.app_tutorias.domain.model.Subject
import com.example.app_tutorias.domain.repository.AuthRepository
import com.example.app_tutorias.domain.repository.CatalogRepository
import com.example.app_tutorias.domain.repository.FavoritesRepository
import com.example.app_tutorias.domain.repository.SearchHistoryRepository
import com.example.app_tutorias.domain.repository.SessionRepository
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TutorLinkViewModel(
    private val catalogRepository: CatalogRepository,
    private val sessionRepository: SessionRepository,
    private val favoritesRepository: FavoritesRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private var subjects: List<Subject> = emptyList()
    private var tutors: List<Tutor> = emptyList()
    private var remoteTutors: List<com.example.app_tutorias.domain.model.Tutor> = emptyList()
    private var catalogLoadState = UiLoadState.LOADING
    private var catalogError: String? = null
    private var sessionLoadState = UiLoadState.LOADING
    private var sessionError: String? = null
    private var bookingJob: Job? = null
    private var reservationId: String? = null
    private var userId: String? = null
    private var searchHistory: List<String> = emptyList()

    private val _sessions = MutableStateFlow<List<TutoringSession>>(emptyList())
    val sessions = _sessions.asStateFlow()
    private val _homeUiState = MutableStateFlow(HomeUiState(loadState = UiLoadState.LOADING))
    val homeUiState = _homeUiState.asStateFlow()
    private val _searchUiState = MutableStateFlow(SearchUiState(loadState = UiLoadState.LOADING))
    val searchUiState = _searchUiState.asStateFlow()
    private val _calendarUiState = MutableStateFlow(CalendarUiState(loadState = UiLoadState.LOADING))
    val calendarUiState = _calendarUiState.asStateFlow()
    private val _bookingUiState = MutableStateFlow(BookingUiState())
    val bookingUiState = _bookingUiState.asStateFlow()
    private val reloadRequests = MutableStateFlow(0)

    init {
        // Room keeps the history per user and works without a connection,
        // so it is observed apart from the Firestore streams.
        viewModelScope.launch {
            searchHistoryRepository.observeHistory()
                .catch { emit(emptyList()) }
                .collect { history ->
                    searchHistory = history.map { it.query }
                    refreshStates()
                }
        }
        viewModelScope.launch {
            authRepository.currentUserId.distinctUntilChanged().collectLatest { currentUserId ->
                userId = currentUserId
                bookingJob?.cancel()
                _bookingUiState.value = BookingUiState()
                reservationId = null
                subjects = emptyList()
                tutors = emptyList()
                remoteTutors = emptyList()
                _sessions.value = emptyList()
                _searchUiState.value = SearchUiState()
                _calendarUiState.value = CalendarUiState()
                catalogError = null
                sessionError = null
                catalogLoadState = UiLoadState.LOADING
                sessionLoadState = UiLoadState.LOADING
                refreshStates()
                if (currentUserId != null) {
                    reloadRequests.collectLatest {
                        observeData()
                    }
                }
            }
        }
    }

    fun retryLoading() {
        reloadRequests.update { it + 1 }
    }

    private suspend fun observeData() = coroutineScope {
        catalogError = null
        sessionError = null
        catalogLoadState = UiLoadState.LOADING
        sessionLoadState = UiLoadState.LOADING
        refreshStates()
        launch {
            observeWithDeadline(
                source = combine(catalogRepository.observeTutors(), catalogRepository.observeSubjects()) { tutors, subjects ->
                    tutors to subjects
                },
                onError = {
                    catalogLoadState = UiLoadState.ERROR
                    catalogError = "No pudimos cargar los tutores. Revisa la conexión y los permisos de Firebase."
                    refreshStates()
                }
            ) { (loadedTutors, loadedSubjects) ->
                subjects = loadedSubjects
                remoteTutors = loadedTutors
                tutors = loadedTutors.map { it.toUiModel(subjects) }
                catalogLoadState = if (tutors.isEmpty()) UiLoadState.EMPTY else UiLoadState.CONTENT
                catalogError = null
                refreshStates()
            }
        }
        launch {
            observeWithDeadline(
                source = sessionRepository.observeMySessions(),
                onError = {
                    sessionLoadState = UiLoadState.ERROR
                    sessionError = "No pudimos cargar tus tutorías. Revisa la conexión e inténtalo de nuevo."
                    refreshStates()
                }
            ) { loadedSessions ->
                _sessions.value = loadedSessions.map { it.toUiModel() }
                val savedReservation = loadedSessions.firstOrNull { it.id == reservationId }
                if (savedReservation != null && !savedReservation.isPendingSync) {
                    _bookingUiState.update { it.copy(isPendingSync = false) }
                }
                sessionLoadState = UiLoadState.CONTENT
                sessionError = null
                refreshStates()
            }
        }
    }

    // Stop showing a spinner if the initial snapshot never arrives, but keep
    // listening so that the screen recovers when connectivity returns.
    private suspend fun <T> observeWithDeadline(
        source: Flow<T>,
        onError: () -> Unit,
        onValue: (T) -> Unit
    ) = coroutineScope {
        val deadline = launch {
            delay(12_000L)
            onError()
        }
        try {
            source.catch {
                deadline.cancel()
                onError()
            }.collect {
                deadline.cancel()
                onValue(it)
            }
        } finally {
            deadline.cancel()
        }
    }

    fun tutorById(tutorId: String): Tutor? = tutors.firstOrNull { it.id == tutorId }

    fun onSearchQueryChanged(query: String) {
        _searchUiState.update { it.copy(query = query) }
        refreshStates()
    }

    fun clearSearchQuery() = onSearchQueryChanged("")

    fun onSearchSubmitted() = saveSearch(_searchUiState.value.query)

    fun onSearchHistorySelected(query: String) {
        onSearchQueryChanged(query)
        saveSearch(query)
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            try {
                searchHistoryRepository.clearHistory()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // Nothing to clear when there is no active user.
            }
        }
    }

    private fun saveSearch(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            try {
                searchHistoryRepository.saveSearch(query)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // The history is a convenience; a failed local write must not block searching.
            }
        }
    }

    fun onSearchCategorySelected(categoryId: String?) {
        _searchUiState.update {
            it.copy(selectedCategoryId = if (categoryId == it.selectedCategoryId) null else categoryId)
        }
        refreshStates()
    }

    fun onSessionFilterSelected(filter: SessionFilter) {
        _calendarUiState.update { it.copy(selectedFilter = filter) }
        refreshStates()
    }

    fun beginBooking(tutorId: String, destinationId: String) {
        if (_bookingUiState.value.destinationId != destinationId && !_bookingUiState.value.isSubmitting) {
            reservationId = null
            _bookingUiState.value = BookingUiState(tutorId = tutorId, destinationId = destinationId)
        }
    }

    fun onBookingTopicChanged(topic: String) = updateBookingState(_bookingUiState.value.form.copy(topic = topic), topicTouched = true)
    fun onBookingDateChanged(date: String) = updateBookingState(_bookingUiState.value.form.copy(date = date), dateTouched = true)
    fun onBookingTimeChanged(time: String) = updateBookingState(_bookingUiState.value.form.copy(time = time), timeTouched = true)
    fun onBookingNotesChanged(notes: String) = updateBookingState(_bookingUiState.value.form.copy(notes = notes), notesTouched = true)

    fun submitBooking(tutor: Tutor) {
        val currentState = _bookingUiState.value
        val form = currentState.form.normalized()
        val validation = validateBookingForm(form)
        if (currentState.isSubmitting || currentState.reservationComplete) return
        if (!validation.isValid) {
            _bookingUiState.value = currentState.copy(validation = validation, topicTouched = true, dateTouched = true, timeTouched = true, notesTouched = true)
            return
        }
        val currentUserId = userId ?: return
        val subjectId = remoteTutors.firstOrNull { it.id == tutor.id }?.subjectIds?.firstOrNull()
        if (subjectId == null) {
            _bookingUiState.update { it.copy(errorMessage = "Este tutor no tiene una materia asignada. No se puede reservar todavía.") }
            return
        }
        val startAt = LocalDate.parse(form.date).atTime(LocalTime.parse(form.time)).atZone(ZoneId.systemDefault()).toInstant()
        val id = reservationId ?: UUID.randomUUID().toString().also { reservationId = it }
        val session = com.example.app_tutorias.domain.model.TutoringSession(
            id = id,
            studentId = currentUserId,
            studentName = "",
            tutorId = tutor.id,
            tutorName = tutor.name,
            subjectId = subjectId,
            subjectName = form.topic,
            startAt = startAt,
            endAt = startAt.plusSeconds(3600),
            notes = form.notes,
            status = com.example.app_tutorias.domain.model.TutoringStatus.UPCOMING,
            createdAt = Instant.now()
        )
        _bookingUiState.value = currentState.copy(form = form, validation = validation, isSubmitting = true, errorMessage = null)
        bookingJob = viewModelScope.launch {
            try {
                val result = sessionRepository.createSession(session).getOrThrow()
                _bookingUiState.update {
                    it.copy(isSubmitting = false, reservationComplete = true, isPendingSync = result.isPending)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _bookingUiState.update {
                    it.copy(isSubmitting = false, errorMessage = "No pudimos guardar la reserva. Revisa la conexión y los permisos e inténtalo de nuevo.")
                }
            }
        }
    }

    private fun refreshStates() {
        val categories = subjects.map { it.toUiModel() }
        _homeUiState.value = HomeUiState(
            categories = categories,
            recommendedTutors = tutors.take(3),
            nextSession = _sessions.value.filter { it.status == TutoringStatus.UPCOMING }.minWithOrNull(compareBy(TutoringSession::date, TutoringSession::time)),
            loadState = catalogLoadState,
            errorMessage = catalogError ?: sessionError
        )
        _searchUiState.update {
            it.copy(categories = categories, filteredTutors = filterTutors(tutors, it.query, it.selectedCategoryId), history = searchHistory, loadState = catalogLoadState, errorMessage = catalogError)
        }
        _calendarUiState.update { state ->
            val visible = when (state.selectedFilter) {
                SessionFilter.UPCOMING -> _sessions.value.filter { it.status == TutoringStatus.UPCOMING }.sortedWith(compareBy(TutoringSession::date, TutoringSession::time))
                SessionFilter.COMPLETED -> _sessions.value.filter { it.status == TutoringStatus.COMPLETED }.sortedWith(compareByDescending(TutoringSession::date).thenByDescending { it.time })
            }
            state.copy(visibleSessions = visible, loadState = if (sessionLoadState == UiLoadState.CONTENT && visible.isEmpty()) UiLoadState.EMPTY else sessionLoadState, errorMessage = sessionError)
        }
    }

    private fun updateBookingState(
        form: BookingFormData,
        topicTouched: Boolean = _bookingUiState.value.topicTouched,
        dateTouched: Boolean = _bookingUiState.value.dateTouched,
        timeTouched: Boolean = _bookingUiState.value.timeTouched,
        notesTouched: Boolean = _bookingUiState.value.notesTouched
    ) {
        if (_bookingUiState.value.isSubmitting || _bookingUiState.value.reservationComplete) return
        _bookingUiState.update {
            it.copy(form = form, validation = validateBookingForm(form), topicTouched = topicTouched, dateTouched = dateTouched, timeTouched = timeTouched, notesTouched = notesTouched, errorMessage = null)
        }
    }
}
