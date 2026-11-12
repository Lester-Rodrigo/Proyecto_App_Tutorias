package com.example.tutorlink.domain.repository

import com.example.tutorlink.domain.model.Subject
import com.example.tutorlink.domain.model.Tutor
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    fun observeTutors(): Flow<List<Tutor>>

    fun observeTutor(
        tutorId: String
    ): Flow<Tutor?>

    fun observeSubjects(): Flow<List<Subject>>
}