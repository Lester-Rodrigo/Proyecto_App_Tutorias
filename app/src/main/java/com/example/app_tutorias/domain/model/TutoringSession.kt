package com.example.app_tutorias.domain.model

import java.time.Instant

enum class TutoringStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
}

data class TutoringSession(
    val id: String,
    val studentId: String,
    val studentName: String,
    val tutorId: String,
    val tutorName: String,
    val subjectId: String,
    val subjectName: String,
    val startAt: Instant,
    val endAt: Instant,
    val notes: String,
    val status: TutoringStatus,
    val createdAt: Instant,
    val isPendingSync: Boolean = false
)
