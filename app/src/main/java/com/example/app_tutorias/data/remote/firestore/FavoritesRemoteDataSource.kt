package com.example.app_tutorias.data.remote.firestore

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FavoritesRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun favoritesCollection() =
        firestore
            .collection("users")
            .document(requireUserId())
            .collection("favorites")

    fun observeFavorites(): Flow<List<FavoriteTutorDto>> =
        callbackFlow {
            if (auth.currentUser == null) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }
            val registration = favoritesCollection()
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val favorites = snapshot
                        ?.documents
                        ?.mapNotNull { document ->
                            document
                                .toObject(FavoriteTutorDto::class.java)
                                ?.copy(tutorId = document.id)
                        }
                        .orEmpty()
                    trySend(favorites)
                }
            awaitClose { registration.remove() }
        }

    suspend fun addFavorite(
        tutorId: String,
        tutorName: String
    ) {
        favoritesCollection()
            .document(tutorId)
            .set(
                FavoriteTutorDto(
                    tutorId = tutorId,
                    tutorName = tutorName,
                    addedAt = Timestamp.now()
                )
            )
            .await()
    }

    suspend fun removeFavorite(tutorId: String) {
        favoritesCollection()
            .document(tutorId)
            .delete()
            .await()
    }

    private fun requireUserId(): String =
        requireNotNull(auth.currentUser?.uid) { "No existe una sesión activa" }
}