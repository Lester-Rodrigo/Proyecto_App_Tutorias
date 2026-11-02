package com.example.app_tutorias.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import com.example.app_tutorias.domain.model.SessionWriteResult

class SessionRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val writeErrors = MutableSharedFlow<Throwable>(extraBufferCapacity = 1)
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
            val errorObserver = launch { writeErrors.collect { close(it) } }
            val registration = sessionsCollection
                .whereEqualTo("studentId", userId)
                .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val sessions = runCatching {
                        snapshot?.documents?.mapNotNull { document ->
                            document.toObject(SessionDto::class.java)
                                ?.copy(
                                    id = document.id,
                                    isPendingSync = document.metadata.hasPendingWrites()
                                )
                        }.orEmpty()
                    }
                    sessions.onSuccess { trySend(it) }.onFailure { close(it) }
                }
            awaitClose {
                registration.remove()
                errorObserver.cancel()
            }
        }

    suspend fun createSession(
        session: SessionDto
    ): SessionWriteResult {
        val userId = requireNotNull(auth.currentUser?.uid) { "No existe una sesión de usuario activa" }
        val document = if (session.id.isBlank()) {
            sessionsCollection.document()
        } else {
            sessionsCollection.document(session.id)
        }

        val writeTask = document.set(
            session.copy(
                id = document.id,
                studentId = userId,
                studentName = auth.currentUser?.displayName
                    ?: auth.currentUser?.email.orEmpty()
            )
        )
        writeTask.addOnFailureListener { writeErrors.tryEmit(it) }
        // A timed-out await does not cancel Firestore's persisted write queue.
        val acknowledged = withTimeoutOrNull(10_000L) {
            writeTask.await()
            true
        } ?: false
        return SessionWriteResult(document.id, isPending = !acknowledged)
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
