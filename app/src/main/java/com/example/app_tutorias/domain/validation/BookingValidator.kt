package com.example.app_tutorias.domain.validation

import com.example.app_tutorias.domain.model.TutoringSession

object BookingValidator {
    fun validate(session: TutoringSession) {
        require(session.endAt.isAfter(session.startAt)) {
            "La duración de la tutoría debe ser mayor que cero."
        }
    }
}
