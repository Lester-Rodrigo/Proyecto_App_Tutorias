package com.example.app_tutorias

import com.example.app_tutorias.domain.model.TutoringSession
import com.example.app_tutorias.domain.model.TutoringStatus
import com.example.app_tutorias.domain.validation.BookingValidator
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BookingValidatorTest {
    private val startAt = Instant.parse("2026-11-06T16:00:00Z")

    @Test
    fun `zero minute duration is rejected`() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            BookingValidator.validate(sessionEndingAt(startAt))
        }

        assertEquals("La duración de la tutoría debe ser mayor que cero.", error.message)
    }

    @Test
    fun `negative duration is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            BookingValidator.validate(sessionEndingAt(startAt.minusSeconds(60)))
        }
    }

    @Test
    fun `sixty minute duration is accepted`() {
        BookingValidator.validate(sessionEndingAt(startAt.plusSeconds(60 * 60)))
    }

    @Test
    fun `positive duration below one minute is accepted without truncation`() {
        BookingValidator.validate(sessionEndingAt(startAt.plusSeconds(30)))
    }

    private fun sessionEndingAt(endAt: Instant) = TutoringSession(
        id = "session-1",
        studentId = "student-1",
        studentName = "Estudiante",
        tutorId = "tutor-1",
        tutorName = "Tutor",
        subjectId = "math",
        subjectName = "Matemáticas",
        startAt = startAt,
        endAt = endAt,
        notes = "",
        status = TutoringStatus.UPCOMING,
        createdAt = startAt.minusSeconds(3600)
    )
}
