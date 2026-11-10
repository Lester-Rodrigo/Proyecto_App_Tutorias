package com.example.app_tutorias.domain.repository

import com.example.app_tutorias.domain.model.Subject
import com.example.app_tutorias.domain.model.Tutor
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    fun observeTutors(): Flow<List<Tutor>>

    fun observeTutor(
        tutorId: String
    ): Flow<Tutor?>

    fun observeSubjects(): Flow<List<Subject>>
}