package com.example.tutorlink.domain.repository

import com.example.tutorlink.domain.model.TutoringSession
import com.example.tutorlink.domain.model.TutoringStatus
import com.example.tutorlink.domain.model.SessionWriteResult
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeMySessions(): Flow<List<TutoringSession>>

    suspend fun createSession(
        session: TutoringSession
    ): Result<SessionWriteResult>

    suspend fun updateSessionStatus(
        sessionId: String,
        status: TutoringStatus
    ): Result<Unit>

    suspend fun deleteSession(
        sessionId: String
    ): Result<Unit>
}
