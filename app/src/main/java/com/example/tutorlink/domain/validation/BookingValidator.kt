package com.example.tutorlink.domain.validation

import com.example.tutorlink.domain.model.TutoringSession

object BookingValidator {
    fun validate(session: TutoringSession) {
        require(session.endAt.isAfter(session.startAt)) {
            "La duración de la tutoría debe ser mayor que cero."
        }
    }
}
