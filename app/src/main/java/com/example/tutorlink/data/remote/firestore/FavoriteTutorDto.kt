package com.example.tutorlink.data.remote.firestore

import com.google.firebase.Timestamp

data class FavoriteTutorDto(
    val tutorId: String = "",
    val tutorName: String = "",
    val addedAt: Timestamp = Timestamp(0, 0)
)