package com.example.tutorlink.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorlink.data.BookingFormData
import com.example.tutorlink.data.Tutor
import com.example.tutorlink.data.normalized
import com.example.tutorlink.data.validateBookingForm
import com.example.tutorlink.domain.repository.SessionRepository
import com.example.tutorlink.domain.repository.TutorLinkRepository
import com.example.tutorlink.ui.BookingUiState
import com.example.tutorlink.ui.TutorDetailUiState
import com.example.tutorlink.ui.UiLoadState
import com.example.tutorlink.ui.toUiModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TutorDetailViewModel(
    private val repository: TutorLinkRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TutorDetailUiState())
    val uiState = _uiState.asStateFlow()
    private val _bookingUiState = MutableStateFlow(BookingUiState())
    val bookingUiState = _bookingUiState.asStateFlow()
    private var userId: String? = null
    private var bookingJob: Job? = null
    private var reservationId: String? = null

    init {
        viewModelScope.launch {
            repository.data.collect { data ->
                if (userId != data.userId) {
                    userId = data.userId
                    bookingJob?.cancel()
                    reservationId = null
                    _bookingUiState.value = BookingUiState()
                }
                _uiState.value = TutorDetailUiState(
                    tutors = data.tutors.map { it.toUiModel(data.subjects) },
                    loadState = UiLoadState.valueOf(data.catalogLoadState.name),
                    errorMessage = data.catalogError
                )
                val saved = data.sessions.firstOrNull { it.id == reservationId }
                if (saved != null && !saved.isPendingSync) {
                    _bookingUiState.update { it.copy(isPendingSync = false) }
                }
            }
        }
    }

    fun tutorById(tutorId: String): Tutor? = _uiState.value.tutors.firstOrNull { it.id == tutorId }
    fun retryLoading() = repository.retryLoading()

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
        val currentUserId = repository.data.value.userId ?: return
        val subjectId = repository.data.value.tutors.firstOrNull { it.id == tutor.id }?.subjectIds?.firstOrNull()
        if (subjectId == null) {
            _bookingUiState.update { it.copy(errorMessage = "Este tutor no tiene una materia asignada. No se puede reservar todavía.") }
            return
        }
        val startAt = LocalDate.parse(form.date).atTime(LocalTime.parse(form.time)).atZone(ZoneId.systemDefault()).toInstant()
        val id = reservationId ?: UUID.randomUUID().toString().also { reservationId = it }
        val session = com.example.tutorlink.domain.model.TutoringSession(
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
            status = com.example.tutorlink.domain.model.TutoringStatus.UPCOMING,
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
