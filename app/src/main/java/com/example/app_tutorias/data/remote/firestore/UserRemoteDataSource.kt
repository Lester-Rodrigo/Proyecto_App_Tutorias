package com.example.app_tutorias.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val usersCollection
        get() = firestore.collection(USERS_COLLECTION)

    fun observeCurrentUser(): Flow<UserDto?> =
        callbackFlow {
            val userId = auth.currentUser?.uid

            if (userId == null) {
                trySend(null)
                close()
                return@callbackFlow
            }
            val registration = usersCollection
                .document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val user = snapshot
                        ?.toObject(UserDto::class.java)
                        ?.copy(id = userId)
                    trySend(user)
                }
            awaitClose { registration.remove() }
        }

    suspend fun saveUser(user: UserDto) {
        usersCollection
            .document(user.id)
            .set(user)
            .await()
    }

    suspend fun updateUser(user: UserDto) {
        val userId = requireNotNull(auth.currentUser?.uid) { "No existe una sesión activa" }
        usersCollection
            .document(userId)
            .set(user.copy(id = userId))
            .await()
    }

    private companion object { const val USERS_COLLECTION = "users" }
}