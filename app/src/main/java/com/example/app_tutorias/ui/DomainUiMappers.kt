package com.example.app_tutorias.ui

import com.example.app_tutorias.data.SubjectCategory
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.data.TutoringSession
import com.example.app_tutorias.data.TutoringStatus
import com.example.app_tutorias.domain.model.Subject
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

fun Subject.toUiModel() = SubjectCategory(id, name, emoji)

fun com.example.app_tutorias.domain.model.Tutor.toUiModel(subjects: List<Subject>) = Tutor(
    id = id,
    name = name,
    initials = initials,
    specialty = subjects.filter { it.id in subjectIds }.joinToString { it.name },
    description = description,
    rating = averageRating,
    pricePerHour = pricePerHour,
    availability = availability.joinToString { slot ->
        val day = if (slot.dayOfWeek in 1..7) {
            DayOfWeek.of(slot.dayOfWeek).getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"))
        } else {
            "Horario"
        }
        "$day · ${slot.startTime}–${slot.endTime}"
    }.ifBlank { "Consulta la disponibilidad con el tutor" },
    categoryIds = subjectIds
)

fun com.example.app_tutorias.domain.model.TutoringSession.toUiModel(): TutoringSession {
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
