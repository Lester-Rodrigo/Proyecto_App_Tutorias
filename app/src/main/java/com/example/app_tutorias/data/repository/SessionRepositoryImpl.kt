package com.example.app_tutorias.data.repository

import com.example.app_tutorias.data.mapper.toDomain
import com.example.app_tutorias.data.mapper.toDto
import com.example.app_tutorias.data.remote.firestore.SessionRemoteDataSource
import com.example.app_tutorias.domain.model.TutoringSession
import com.example.app_tutorias.domain.model.TutoringStatus
import com.example.app_tutorias.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionRepositoryImpl(
    private val remoteDataSource: SessionRemoteDataSource
) : SessionRepository {

    override fun observeMySessions(): Flow<List<TutoringSession>> =
        remoteDataSource.observeMySessions().map { sessions ->
            sessions
                .map { it.toDomain() }
                .sortedBy { it.startAt }
        }

    override suspend fun createSession(
        session: TutoringSession
    ): Result<String> = runCatching { remoteDataSource.createSession(session.toDto()) }

    override suspend fun updateSessionStatus(
        sessionId: String,
        status: TutoringStatus
    ): Result<Unit> = runCatching {
        remoteDataSource.updateSessionStatus(
            sessionId = sessionId,
            status = status.name
        )
    }

    override suspend fun deleteSession(
        sessionId: String
    ): Result<Unit> = runCatching { remoteDataSource.deleteSession(sessionId) }
}