package com.example.app_tutorias.data.remote.firestore

import com.google.firebase.Timestamp

data class SessionDto(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val tutorId: String = "",
    val tutorName: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val startAt: Timestamp = Timestamp(0, 0),
    val endAt: Timestamp = Timestamp(0, 0),
    val notes: String = "",
    val status: String = "UPCOMING",
    val createdAt: Timestamp = Timestamp(0, 0)
)