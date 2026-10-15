package com.example.app_tutorias.ui

import androidx.lifecycle.ViewModel
import com.example.app_tutorias.data.MockData
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.data.TutoringSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TutorLinkViewModel : ViewModel() {
    val categories = MockData.categories
    val tutors = MockData.tutors

    private val _sessions = MutableStateFlow(MockData.initialSessions())
    val sessions = _sessions.asStateFlow()

    fun tutorById(tutorId: String): Tutor? =
        tutors.firstOrNull { it.id == tutorId }

    fun addReservation(session: TutoringSession) {
        _sessions.update { currentSessions ->
            if (currentSessions.any { it.id == session.id }) {
                currentSessions
            } else {
                currentSessions + session
            }
        }
    }
}
