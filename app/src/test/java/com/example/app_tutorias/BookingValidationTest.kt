package com.example.app_tutorias

import com.example.app_tutorias.data.BookingFieldError
import com.example.app_tutorias.data.BookingFormData
import com.example.app_tutorias.data.MAX_NOTES_LENGTH
import com.example.app_tutorias.data.validateBookingForm
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BookingValidationTest {
    private val today = LocalDate.of(2026, 9, 20)

    @Test
    fun `empty form is invalid and disables booking`() {
        val result = validateBookingForm(BookingFormData(), today)

        assertFalse(result.isValid)
        assertEquals(BookingFieldError.REQUIRED, result.topicError)
        assertEquals(BookingFieldError.REQUIRED, result.dateError)
        assertEquals(BookingFieldError.REQUIRED, result.timeError)
    }

    @Test
    fun `valid required fields enable booking`() {
        val result = validateBookingForm(
            BookingFormData(
                topic = "Cálculo integral",
                date = "2026-09-21",
                time = "16:30",
                notes = "Repasar integración por partes."
            ),
            today
        )

        assertTrue(result.isValid)
        assertNull(result.topicError)
        assertNull(result.dateError)
        assertNull(result.timeError)
        assertNull(result.notesError)
    }

    @Test
    fun `past or malformed dates are rejected`() {
        val past = validateBookingForm(validForm(date = "2026-09-19"), today)
        val malformed = validateBookingForm(validForm(date = "20-09-2026"), today)

        assertEquals(BookingFieldError.PAST_DATE, past.dateError)
        assertEquals(BookingFieldError.INVALID_DATE, malformed.dateError)
        assertFalse(past.isValid)
        assertFalse(malformed.isValid)
    }

    @Test
    fun `short topic invalid time and long notes are rejected`() {
        val result = validateBookingForm(
            validForm(
                topic = "OK",
                time = "25:70",
                notes = "a".repeat(MAX_NOTES_LENGTH + 1)
            ),
            today
        )

        assertEquals(BookingFieldError.TOO_SHORT, result.topicError)
        assertEquals(BookingFieldError.INVALID_TIME, result.timeError)
        assertEquals(BookingFieldError.NOTES_TOO_LONG, result.notesError)
        assertFalse(result.isValid)
    }

    private fun validForm(
        topic: String = "Álgebra",
        date: String = "2026-09-20",
        time: String = "09:00",
        notes: String = ""
    ) = BookingFormData(topic, date, time, notes)
}
