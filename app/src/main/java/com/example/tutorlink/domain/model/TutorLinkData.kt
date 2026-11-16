package com.example.tutorlink.domain.model

enum class DataLoadState { LOADING, CONTENT, EMPTY, ERROR }

data class TutorLinkData(
    val userId: String? = null,
    val userName: String? = null,
    val subjects: List<Subject> = emptyList(),
    val tutors: List<Tutor> = emptyList(),
    val sessions: List<TutoringSession> = emptyList(),
    val catalogLoadState: DataLoadState = DataLoadState.LOADING,
    val catalogError: String? = null,
    val sessionLoadState: DataLoadState = DataLoadState.LOADING,
    val sessionError: String? = null
)
