package com.example.tutorlink.ui

import com.example.tutorlink.data.BookingFormData
import com.example.tutorlink.data.BookingValidationResult
import com.example.tutorlink.data.SubjectCategory
import com.example.tutorlink.data.Tutor
import com.example.tutorlink.data.TutoringSession
import com.example.tutorlink.data.validateBookingForm

data class HomeUiState(
    val categories: List<SubjectCategory> = emptyList(),
    val recommendedTutors: List<Tutor> = emptyList(),
    val nextSession: TutoringSession? = null,
    val userName: String? = null,
    val loadState: UiLoadState = UiLoadState.CONTENT,
    val errorMessage: String? = null
)

data class SearchUiState(
    val query: String = "",
    val selectedCategoryId: String? = null,
    val categories: List<SubjectCategory> = emptyList(),
    val filteredTutors: List<Tutor> = emptyList(),
    val history: List<String> = emptyList(),
    val loadState: UiLoadState = UiLoadState.CONTENT,
    val errorMessage: String? = null
) {
    val hasActiveFilter: Boolean
        get() = query.isNotBlank() || selectedCategoryId != null
}

enum class SessionFilter {
    UPCOMING,
    COMPLETED
}

enum class UiLoadState {
    LOADING,
    CONTENT,
    EMPTY,
    ERROR
}

data class CalendarUiState(
    val selectedFilter: SessionFilter = SessionFilter.UPCOMING,
    val visibleSessions: List<TutoringSession> = emptyList(),
    val loadState: UiLoadState = UiLoadState.CONTENT,
    val errorMessage: String? = null
)

data class BookingUiState(
    val tutorId: String? = null,
    val destinationId: String? = null,
    val form: BookingFormData = BookingFormData(),
    val validation: BookingValidationResult = validateBookingForm(form),
    val topicTouched: Boolean = false,
    val dateTouched: Boolean = false,
    val timeTouched: Boolean = false,
    val notesTouched: Boolean = false,
    val isSubmitting: Boolean = false,
    val reservationComplete: Boolean = false,
    val isPendingSync: Boolean = false,
    val errorMessage: String? = null
) {
    val canSubmit: Boolean
        get() = validation.isValid && !isSubmitting && !reservationComplete
}

data class TutorDetailUiState(
    val tutors: List<Tutor> = emptyList(),
    val loadState: UiLoadState = UiLoadState.LOADING,
    val errorMessage: String? = null
)
