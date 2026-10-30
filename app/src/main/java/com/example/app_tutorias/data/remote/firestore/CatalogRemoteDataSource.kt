package com.example.app_tutorias.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CatalogRemoteDataSource(
    private val firestore: FirebaseFirestore
) {
    fun observeTutors(): Flow<List<TutorDto>> =
        callbackFlow {
            val registration = firestore
                .collection(TUTORS_COLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val tutors = runCatching {
                        snapshot?.documents?.mapNotNull { document ->
                            document.toObject(TutorDto::class.java)?.copy(id = document.id)
                        }.orEmpty()
                    }
                    tutors.onSuccess { trySend(it) }.onFailure { close(it) }
                }
            awaitClose { registration.remove() }
        }

    fun observeTutor(tutorId: String): Flow<TutorDto?> =
        callbackFlow {
            val registration = firestore
                .collection(TUTORS_COLLECTION)
                .document(tutorId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val tutor = runCatching {
                        snapshot?.toObject(TutorDto::class.java)?.copy(id = tutorId)
                    }

                    tutor.onSuccess { trySend(it) }.onFailure { close(it) }
                }
            awaitClose { registration.remove() }
        }

    fun observeSubjects(): Flow<List<SubjectDto>> =
        callbackFlow {
            val registration = firestore
                .collection(SUBJECTS_COLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val subjects = runCatching {
                        snapshot?.documents?.mapNotNull { document ->
                            document.toObject(SubjectDto::class.java)?.copy(id = document.id)
                        }.orEmpty()
                    }
                    subjects.onSuccess { trySend(it) }.onFailure { close(it) }
                }
            awaitClose { registration.remove() }
        }

    private companion object {
        const val TUTORS_COLLECTION = "tutors"
        const val SUBJECTS_COLLECTION = "subjects"
    }
}
