package com.example.app_tutorias.data

import java.time.LocalDate
import java.time.format.DateTimeParseException

data class BookingFormData(
    val topic: String = "",
    val date: String = "",
    val time: String = "",
    val notes: String = ""
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
    val topicError = when {
        form.topic.isBlank() -> BookingFieldError.REQUIRED
        form.topic.trim().length < 3 -> BookingFieldError.TOO_SHORT
        else -> null
    }

    val dateError = when {
        form.date.isBlank() -> BookingFieldError.REQUIRED
        else -> try {
            if (LocalDate.parse(form.date).isBefore(today)) {
                BookingFieldError.PAST_DATE
            } else {
                null
            }
        } catch (_: DateTimeParseException) {
            BookingFieldError.INVALID_DATE
        }
    }

    val timeError = when {
        form.time.isBlank() -> BookingFieldError.REQUIRED
        !TIME_PATTERN.matches(form.time.trim()) -> BookingFieldError.INVALID_TIME
        else -> null
    }

    val notesError = if (form.notes.length > MAX_NOTES_LENGTH) {
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
