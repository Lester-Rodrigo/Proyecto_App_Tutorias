package com.example.app_tutorias.data

import java.time.LocalDate
import java.time.format.DateTimeParseException

data class BookingFormData(
    val topic: String = "",
    val date: String = "",
    val time: String = "",
    val notes: String = ""
)

fun BookingFormData.normalized(): BookingFormData = copy(
    topic = topic.trim(),
    date = date.trim(),
    time = time.trim(),
    notes = notes.trim()
)

enum class BookingFieldError {
    REQUIRED,
    TOO_SHORT,
    INVALID_DATE,
    PAST_DATE,
    INVALID_TIME,
    NOTES_TOO_LONG
}

data class BookingValidationResult(
    val topicError: BookingFieldError? = null,
    val dateError: BookingFieldError? = null,
    val timeError: BookingFieldError? = null,
    val notesError: BookingFieldError? = null
) {
    val isValid: Boolean
        get() = topicError == null && dateError == null &&
            timeError == null && notesError == null
}

fun validateBookingForm(
    form: BookingFormData,
    today: LocalDate = LocalDate.now()
): BookingValidationResult {
    val normalizedForm = form.normalized()

    val topicError = when {
        normalizedForm.topic.isBlank() -> BookingFieldError.REQUIRED
        normalizedForm.topic.length < 3 -> BookingFieldError.TOO_SHORT
        else -> null
    }

    val dateError = when {
        normalizedForm.date.isBlank() -> BookingFieldError.REQUIRED
        else -> try {
            if (LocalDate.parse(normalizedForm.date).isBefore(today)) {
                BookingFieldError.PAST_DATE
            } else {
                null
            }
        } catch (_: DateTimeParseException) {
            BookingFieldError.INVALID_DATE
        }
    }

    val timeError = when {
        normalizedForm.time.isBlank() -> BookingFieldError.REQUIRED
        !TIME_PATTERN.matches(normalizedForm.time) -> BookingFieldError.INVALID_TIME
        else -> null
    }

    val notesError = if (normalizedForm.notes.length > MAX_NOTES_LENGTH) {
        BookingFieldError.NOTES_TOO_LONG
    } else {
        null
    }

    return BookingValidationResult(
        topicError = topicError,
        dateError = dateError,
        timeError = timeError,
        notesError = notesError
    )
}

const val MAX_NOTES_LENGTH = 250
private val TIME_PATTERN = Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$")
