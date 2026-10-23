package com.example.app_tutorias.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SessionRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val sessionsCollection
        get() = firestore.collection(SESSIONS_COLLECTION)

    fun observeMySessions(): Flow<List<SessionDto>> =
        callbackFlow {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }
            val registration = sessionsCollection
                .whereEqualTo("studentId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val sessions = snapshot
                        ?.documents
                        ?.mapNotNull { document ->
                            document.toObject(SessionDto::class.java)
                                ?.copy(id = document.id)
                        }
                        .orEmpty()
                    trySend(sessions)
                }
            awaitClose {
                registration.remove()
            }
        }

    suspend fun createSession(
        session: SessionDto
    ): String {
        val userId = requireNotNull(auth.currentUser?.uid) { "No existe una sesión de usuario activa" }
        val document = if (session.id.isBlank()) {
            sessionsCollection.document()
        } else {
            sessionsCollection.document(session.id)
        }

        document.set(
            session.copy(
                id = document.id,
                studentId = userId
            )
        ).await()
        return document.id
    }

    suspend fun updateSessionStatus(
        sessionId: String,
        status: String
    ) {
        sessionsCollection
            .document(sessionId)
            .update("status", status)
            .await()
    }

    suspend fun deleteSession(sessionId: String) {
        sessionsCollection
            .document(sessionId)
            .delete()
            .await()
    }

    private companion object { const val SESSIONS_COLLECTION = "sessions" }
}