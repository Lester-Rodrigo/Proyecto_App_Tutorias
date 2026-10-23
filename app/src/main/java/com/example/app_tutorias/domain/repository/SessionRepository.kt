package com.example.app_tutorias.domain.repository

import com.example.app_tutorias.domain.model.TutoringSession
import com.example.app_tutorias.domain.model.TutoringStatus
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeMySessions(): Flow<List<TutoringSession>>

    suspend fun createSession(
        session: TutoringSession
    ): Result<String>

    suspend fun updateSessionStatus(
        sessionId: String,
        status: TutoringStatus
    ): Result<Unit>

    suspend fun deleteSession(
        sessionId: String
    ): Result<Unit>
}