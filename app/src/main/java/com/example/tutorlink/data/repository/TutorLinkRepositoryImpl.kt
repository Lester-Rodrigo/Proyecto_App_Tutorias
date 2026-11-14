package com.example.tutorlink.data.repository

import com.example.tutorlink.domain.model.DataLoadState
import com.example.tutorlink.domain.model.TutorLinkData
import com.example.tutorlink.domain.repository.AuthRepository
import com.example.tutorlink.domain.repository.CatalogRepository
import com.example.tutorlink.domain.repository.SessionRepository
import com.example.tutorlink.domain.repository.TutorLinkRepository
import com.example.tutorlink.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Shares authenticated reads across screens. Screen-specific state remains in its ViewModel.
class TutorLinkRepositoryImpl(
    private val catalogRepository: CatalogRepository,
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    scope: CoroutineScope
) : TutorLinkRepository {
    private val reloadRequests = MutableStateFlow(0)

    override val data = channelFlow {
        authRepository.currentUserId.distinctUntilChanged().collectLatest { userId ->
            var current = TutorLinkData(userId = userId)
            suspend fun publish(transform: (TutorLinkData) -> TutorLinkData) {
                current = transform(current)
                send(current)
            }
            send(current)
            if (userId != null) {
                reloadRequests.collectLatest {
                    publish { it.copy(catalogLoadState = DataLoadState.LOADING,
                        sessionLoadState = DataLoadState.LOADING, catalogError = null, sessionError = null) }
                    coroutineScope {
                        launch {
                            observeWithDeadline(
                                source = userRepository.observeCurrentUserProfile(),
                                onError = { publish { it.copy(userName = null) } }
                            ) { profile ->
                                val name = profile?.takeIf { it.id == userId }?.name?.trim()?.takeIf { it.isNotEmpty() }
                                publish { it.copy(userName = name) }
                            }
                        }
                        launch {
                            observeWithDeadline(
                                source = combine(catalogRepository.observeTutors(), catalogRepository.observeSubjects()) { tutors, subjects ->
                                    tutors to subjects
                                },
                                onError = { publish { it.copy(catalogLoadState = DataLoadState.ERROR,
                                    catalogError = "No pudimos cargar los tutores. Revisa la conexión y los permisos de Firebase.") } }
                            ) { (tutors, subjects) ->
                                publish { it.copy(tutors = tutors, subjects = subjects,
                                    catalogLoadState = if (tutors.isEmpty()) DataLoadState.EMPTY else DataLoadState.CONTENT,
                                    catalogError = null) }
                            }
                        }
                        launch {
                            observeWithDeadline(
                                source = sessionRepository.observeMySessions(),
                                onError = { publish { it.copy(sessionLoadState = DataLoadState.ERROR,
                                    sessionError = "No pudimos cargar tus tutorías. Revisa la conexión e inténtalo de nuevo.") } }
                            ) { sessions ->
                                publish { it.copy(sessions = sessions, sessionLoadState = DataLoadState.CONTENT, sessionError = null) }
                            }
                        }
                    }
                }
            }
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000, replayExpirationMillis = 0), TutorLinkData())

    override fun retryLoading() { reloadRequests.update { it + 1 } }
}

// Keep listening after the initial deadline so cached data or a restored connection can recover.
private suspend fun <T> observeWithDeadline(
    source: Flow<T>,
    onError: suspend () -> Unit,
    onValue: suspend (T) -> Unit
) = coroutineScope {
    val deadline = launch { delay(12_000L); onError() }
    try {
        source.catch { deadline.cancel(); onError() }.collect {
            deadline.cancel()
            onValue(it)
        }
    } finally {
        deadline.cancel()
    }
}
