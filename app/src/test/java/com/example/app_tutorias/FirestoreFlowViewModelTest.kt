package com.example.app_tutorias

import com.example.app_tutorias.domain.model.FavoriteTutor
import com.example.app_tutorias.domain.model.SearchHistoryItem
import com.example.app_tutorias.domain.model.SessionWriteResult
import com.example.app_tutorias.domain.model.Subject
import com.example.app_tutorias.domain.model.Tutor
import com.example.app_tutorias.domain.model.TutoringSession
import com.example.app_tutorias.domain.model.TutoringStatus
import com.example.app_tutorias.domain.repository.AuthRepository
import com.example.app_tutorias.domain.repository.CatalogRepository
import com.example.app_tutorias.domain.repository.FavoritesRepository
import com.example.app_tutorias.domain.repository.SearchHistoryRepository
import com.example.app_tutorias.domain.repository.SessionRepository
import com.example.app_tutorias.ui.SessionFilter
import com.example.app_tutorias.ui.TutorLinkViewModel
import com.example.app_tutorias.ui.UiLoadState
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreFlowViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val auth = FakeAuthRepository()
    private val catalog = FakeCatalogRepository()
    private val sessions = FakeSessionRepository()

    @Before
    fun setUp() { Dispatchers.setMain(dispatcher) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `catalog comes from repository and booking appears through session stream`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        runCurrent()
        assertEquals(listOf("Tutor real"), viewModel.searchUiState.value.filteredTutors.map { it.name })
        assertEquals(UiLoadState.CONTENT, viewModel.homeUiState.value.loadState)
        fillBooking(viewModel)
        val tutor = viewModel.searchUiState.value.filteredTutors.single()
        viewModel.submitBooking(tutor)
        viewModel.submitBooking(tutor)
        runCurrent()
        assertEquals(1, sessions.writes.size)
        assertEquals("student-1", sessions.writes.single().studentId)
        assertEquals("math", sessions.writes.single().subjectId)
        assertTrue(viewModel.bookingUiState.value.reservationComplete)
        assertEquals(1, viewModel.calendarUiState.value.visibleSessions.size)
        viewModel.onSessionFilterSelected(SessionFilter.COMPLETED)
        assertEquals(UiLoadState.EMPTY, viewModel.calendarUiState.value.loadState)
    }

    @Test
    fun `failed write shows error and never confirms booking`() = runTest(dispatcher) {
        sessions.failure = IllegalStateException("permission denied")
        val viewModel = createViewModel()
        runCurrent()
        fillBooking(viewModel)
        viewModel.submitBooking(viewModel.searchUiState.value.filteredTutors.single())
        runCurrent()
        assertFalse(viewModel.bookingUiState.value.isSubmitting)
        assertFalse(viewModel.bookingUiState.value.reservationComplete)
        assertNotNull(viewModel.bookingUiState.value.errorMessage)
        assertTrue(viewModel.sessions.value.isEmpty())
    }

    @Test
    fun `pending write is distinguished from server confirmation`() = runTest(dispatcher) {
        sessions.pending = true
        val viewModel = createViewModel()
        runCurrent()
        fillBooking(viewModel)
        viewModel.submitBooking(viewModel.searchUiState.value.filteredTutors.single())
        runCurrent()
        assertTrue(viewModel.bookingUiState.value.isPendingSync)
        assertTrue(viewModel.calendarUiState.value.visibleSessions.single().isPendingSync)
        sessions.sessionFlow.value = sessions.sessionFlow.value.map { it.copy(isPendingSync = false) }
        runCurrent()
        assertFalse(viewModel.bookingUiState.value.isPendingSync)
        assertFalse(viewModel.calendarUiState.value.visibleSessions.single().isPendingSync)
    }

    @Test
    fun `missing first snapshot stops loading and can recover`() = runTest(dispatcher) {
        catalog.tutorFlow = MutableStateFlow<List<Tutor>?>(null).let { source ->
            flow { source.collect { value -> if (value != null) emit(value) } }
        }
        val viewModel = createViewModel()
        runCurrent()
        assertEquals(UiLoadState.LOADING, viewModel.homeUiState.value.loadState)
        advanceTimeBy(12_001)
        runCurrent()
        assertEquals(UiLoadState.ERROR, viewModel.homeUiState.value.loadState)
        catalog.tutorFlow = flowOf(listOf(catalog.tutor))
        viewModel.retryLoading()
        runCurrent()
        assertEquals(UiLoadState.CONTENT, viewModel.homeUiState.value.loadState)
    }

    @Test
    fun `catalog error is visible and signing out clears previous account data`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        runCurrent()
        fillBooking(viewModel)
        viewModel.submitBooking(viewModel.searchUiState.value.filteredTutors.single())
        runCurrent()
        auth.userId.value = null
        runCurrent()
        assertTrue(viewModel.sessions.value.isEmpty())
        assertTrue(viewModel.searchUiState.value.filteredTutors.isEmpty())
        catalog.tutorFlow = flow { throw IllegalStateException("permission denied") }
        auth.userId.value = "student-2"
        runCurrent()
        assertEquals(UiLoadState.ERROR, viewModel.searchUiState.value.loadState)
    }

    private fun fillBooking(viewModel: TutorLinkViewModel) {
        viewModel.beginBooking("tutor-1", "detail-1")
        viewModel.onBookingTopicChanged("Álgebra")
        viewModel.onBookingDateChanged(LocalDate.now().plusDays(1).toString())
        viewModel.onBookingTimeChanged("16:30")
    }

    private fun createViewModel() = TutorLinkViewModel(
        catalogRepository = catalog,
        sessionRepository = sessions,
        favoritesRepository = object : FavoritesRepository {
            override fun observeFavorites(): Flow<List<FavoriteTutor>> = emptyFlow()
            override suspend fun addFavorite(tutorId: String, tutorName: String) = Result.success(Unit)
            override suspend fun removeFavorite(tutorId: String) = Result.success(Unit)
        },
        searchHistoryRepository = object : SearchHistoryRepository {
            override fun observeHistory(): Flow<List<SearchHistoryItem>> = emptyFlow()
            override suspend fun saveSearch(query: String) = Unit
            override suspend fun clearHistory() = Unit
        },
        authRepository = auth
    )
}

private class FakeAuthRepository : AuthRepository {
    val userId = MutableStateFlow<String?>("student-1")
    override val currentUserId: Flow<String?> = userId
    override suspend fun register(name: String, email: String, password: String) = Result.success(Unit)
    override suspend fun signIn(email: String, password: String) = Result.success(Unit)
    override suspend fun signOut() { userId.value = null }
}

private class FakeCatalogRepository : CatalogRepository {
    val tutor = Tutor("tutor-1", "Tutor real", "TR", "Descripción", listOf("math"), 100, 4.5, 2, emptyList())
    var tutorFlow: Flow<List<Tutor>> = flowOf(listOf(tutor))
    override fun observeTutors(): Flow<List<Tutor>> = tutorFlow
    override fun observeTutor(tutorId: String): Flow<Tutor?> = flowOf(tutor)
    override fun observeSubjects(): Flow<List<Subject>> = flowOf(listOf(Subject("math", "Matemáticas", "math", "📐")))
}

private class FakeSessionRepository : SessionRepository {
    val sessionFlow = MutableStateFlow<List<TutoringSession>>(emptyList())
    val writes = mutableListOf<TutoringSession>()
    var failure: Exception? = null
    var pending = false
    override fun observeMySessions(): Flow<List<TutoringSession>> = sessionFlow
    override suspend fun createSession(session: TutoringSession): Result<SessionWriteResult> {
        failure?.let { return Result.failure(it) }
        writes.add(session)
        sessionFlow.value = sessionFlow.value + session.copy(isPendingSync = pending)
        return Result.success(SessionWriteResult(session.id, pending))
    }
    override suspend fun updateSessionStatus(sessionId: String, status: TutoringStatus) = Result.success(Unit)
    override suspend fun deleteSession(sessionId: String) = Result.success(Unit)
}
