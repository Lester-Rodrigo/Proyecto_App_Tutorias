package com.example.app_tutorias.ui

import com.example.app_tutorias.data.BookingFormData
import com.example.app_tutorias.data.BookingValidationResult
import com.example.app_tutorias.data.SubjectCategory
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.data.TutoringSession
import com.example.app_tutorias.data.validateBookingForm

data class HomeUiState(
    val categories: List<SubjectCategory> = emptyList(),
    val recommendedTutors: List<Tutor> = emptyList(),
    val nextSession: TutoringSession? = null,
    val loadState: UiLoadState = UiLoadState.CONTENT,
    val errorMessage: String? = null
)

data class SearchUiState(
    val query: String = "",
    val selectedCategoryId: String? = null,
    val categories: List<SubjectCategory> = emptyList(),
    val filteredTutors: List<Tutor> = emptyList(),
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
    val reservationComplete: Boolean = false
) {
    val canSubmit: Boolean
        get() = validation.isValid && !isSubmitting && !reservationComplete
}
