package com.example.tutorlink.ui

import com.example.tutorlink.data.SubjectCategory
import com.example.tutorlink.data.Tutor
import com.example.tutorlink.data.TutoringSession
import com.example.tutorlink.data.TutoringStatus
import com.example.tutorlink.domain.model.Subject
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

fun Subject.toUiModel() = SubjectCategory(id, name, emoji)

fun com.example.tutorlink.domain.model.Tutor.toUiModel(subjects: List<Subject>): Tutor {
    val availableTimes = availability.map { slot ->
        val day = if (slot.dayOfWeek in 1..7) {
            DayOfWeek.of(slot.dayOfWeek).getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"))
        } else {
            "Horario"
        }
        "$day · ${slot.startTime}–${slot.endTime}"
    }
    return Tutor(
        id = id,
        name = name,
        initials = initials,
        specialty = subjects.filter { it.id in subjectIds }.joinToString { it.name },
        description = description,
        rating = averageRating,
        pricePerHour = pricePerHour,
        availability = availableTimes.joinToString().ifBlank { "Consulta la disponibilidad con el tutor" },
        categoryIds = subjectIds,
        availableTimes = availableTimes
    )
}

fun com.example.tutorlink.domain.model.TutoringSession.toUiModel(): TutoringSession {
    val localStart = startAt.atZone(ZoneId.systemDefault())
    return TutoringSession(
        id = id,
        tutorId = tutorId,
        tutorName = tutorName,
        subject = subjectName,
        date = localStart.toLocalDate(),
        time = localStart.toLocalTime(),
        notes = notes,
        status = TutoringStatus.valueOf(status.name),
        isPendingSync = isPendingSync
    )
}
