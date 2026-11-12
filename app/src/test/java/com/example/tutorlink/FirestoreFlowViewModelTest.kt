package com.example.tutorlink

import com.example.tutorlink.domain.model.FavoriteTutor
import com.example.tutorlink.domain.model.AvailabilitySlot
import com.example.tutorlink.domain.model.UserProfile
import com.example.tutorlink.domain.model.UserRole
import com.example.tutorlink.domain.model.SearchHistoryItem
import com.example.tutorlink.domain.model.SessionWriteResult
import com.example.tutorlink.domain.model.Subject
import com.example.tutorlink.domain.model.Tutor
import com.example.tutorlink.domain.model.TutoringSession
import com.example.tutorlink.domain.model.TutoringStatus
import com.example.tutorlink.domain.repository.AuthRepository
import com.example.tutorlink.domain.repository.CatalogRepository
import com.example.tutorlink.domain.repository.FavoritesRepository
import com.example.tutorlink.domain.repository.SearchHistoryRepository
import com.example.tutorlink.domain.repository.SessionRepository
import com.example.tutorlink.domain.repository.UserRepository
import com.example.tutorlink.ui.SessionFilter
import com.example.tutorlink.ui.TutorLinkViewModel
import com.example.tutorlink.ui.UiLoadState
import java.time.Instant
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreFlowViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val auth = FakeAuthRepository()
    private val users = FakeUserRepository(auth)
    private val catalog = FakeCatalogRepository()
    private val sessions = FakeSessionRepository()
    private val searchHistory = FakeSearchHistoryRepository()

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

    @Test
    fun `submitted searches are saved in history and can be reused`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        runCurrent()
        viewModel.onSearchQueryChanged("Álgebra")
        viewModel.onSearchSubmitted()
        viewModel.onSearchQueryChanged("Kotlin")
        viewModel.onSearchSubmitted()
        viewModel.onSearchQueryChanged("   ")
        viewModel.onSearchSubmitted()
        runCurrent()
        assertEquals(listOf("Kotlin", "Álgebra"), viewModel.searchUiState.value.history)
        viewModel.onSearchHistorySelected("Álgebra")
        runCurrent()
        assertEquals("Álgebra", viewModel.searchUiState.value.query)
        assertEquals(listOf("Álgebra", "Kotlin"), viewModel.searchUiState.value.history)
        viewModel.clearSearchHistory()
        runCurrent()
        assertTrue(viewModel.searchUiState.value.history.isEmpty())
    }

    @Test
    fun `history stays visible when the catalog cannot load`() = runTest(dispatcher) {
        searchHistory.saveSearch("Física")
        catalog.tutorFlow = flow { throw IllegalStateException("offline") }
        val viewModel = createViewModel()
        runCurrent()
        assertEquals(UiLoadState.ERROR, viewModel.searchUiState.value.loadState)
        assertEquals(listOf("Física"), viewModel.searchUiState.value.history)
    }

    @Test
    fun `greeting follows profile updates and clears when changing accounts`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        runCurrent()
        assertEquals("Lucía", viewModel.homeUiState.value.userName)
        users.profiles.getValue("student-1").value = profile("student-1", "  Marta  ")
        runCurrent()
        assertEquals("Marta", viewModel.homeUiState.value.userName)

        auth.userId.value = "student-2"
        runCurrent()
        assertNull(viewModel.homeUiState.value.userName)
        // A late update from the previous account must not reach the current screen.
        users.profiles.getValue("student-1").value = profile("student-1", "Nombre anterior")
        users.profiles.getValue("student-2").value = profile("student-2", "Diego")
        runCurrent()
        assertEquals("Diego", viewModel.homeUiState.value.userName)
        auth.userId.value = null
        runCurrent()
        assertNull(viewModel.homeUiState.value.userName)
    }

    @Test
    fun `missing blank or mismatched profile never supplies a greeting name`() = runTest(dispatcher) {
        users.profiles.getValue("student-1").value = null
        val viewModel = createViewModel()
        runCurrent()
        assertNull(viewModel.homeUiState.value.userName)
        users.profiles.getValue("student-1").value = profile("student-1", "   ")
        runCurrent()
        assertNull(viewModel.homeUiState.value.userName)
        users.profiles.getValue("student-1").value = profile("student-2", "Otro usuario")
        runCurrent()
        assertNull(viewModel.homeUiState.value.userName)
        assertEquals(UiLoadState.CONTENT, viewModel.homeUiState.value.loadState)
    }

    @Test
    fun `profile error does not block catalog or booking and retry restores the name`() = runTest(dispatcher) {
        users.failure = IllegalStateException("offline without cached profile")
        val viewModel = createViewModel()
        runCurrent()
        assertNull(viewModel.homeUiState.value.userName)
        assertEquals(UiLoadState.CONTENT, viewModel.homeUiState.value.loadState)
        fillBooking(viewModel)
        viewModel.submitBooking(viewModel.searchUiState.value.filteredTutors.single())
        runCurrent()
        assertTrue(viewModel.bookingUiState.value.reservationComplete)
        users.failure = null
        viewModel.retryLoading()
        runCurrent()
        assertEquals("Lucía", viewModel.homeUiState.value.userName)
    }

    @Test
    fun `profile deadline keeps catalog usable and a later cached snapshot updates the name`() = runTest(dispatcher) {
        val profileSource = MutableStateFlow<UserProfile?>(null)
        users.source = flow { profileSource.collect { if (it != null) emit(it) } }
        val viewModel = createViewModel()
        runCurrent()
        advanceTimeBy(12_001)
        runCurrent()
        assertNull(viewModel.homeUiState.value.userName)
        assertEquals(UiLoadState.CONTENT, viewModel.homeUiState.value.loadState)
        profileSource.value = profile("student-1", "Nombre en caché")
        runCurrent()
        assertEquals("Nombre en caché", viewModel.homeUiState.value.userName)
    }

    @Test
    fun `detail availability follows repository snapshots without fabricated times`() = runTest(dispatcher) {
        val recommended = (1..3).map { catalog.tutor.copy(id = "recommended-$it", name = "Recomendado $it") }
        val tutorSource = MutableStateFlow(recommended + catalog.tutor.copy(availability = listOf(
            AvailabilitySlot("monday", 1, "15:00", "18:00"),
            AvailabilitySlot("wednesday", 3, "09:00", "11:00")
        )))
        catalog.tutorFlow = tutorSource
        val viewModel = createViewModel()
        runCurrent()
        viewModel.onSearchQueryChanged("Recomendado")
        val homeBefore = viewModel.homeUiState.value
        val searchBefore = viewModel.searchUiState.value
        assertEquals(listOf("lunes · 15:00–18:00", "miércoles · 09:00–11:00"),
            viewModel.tutorCatalog.value.last().availableTimes)
        tutorSource.value = recommended + catalog.tutor.copy(availability = listOf(
            AvailabilitySlot("tuesday", 2, "10:00", "12:00")
        ))
        runCurrent()
        assertEquals(listOf("martes · 10:00–12:00"), viewModel.tutorCatalog.value.last().availableTimes)
        // Detail still updates when its tutor is outside recommendations and search results.
        assertEquals(homeBefore, viewModel.homeUiState.value)
        assertEquals(searchBefore, viewModel.searchUiState.value)
        tutorSource.value = recommended + catalog.tutor
        runCurrent()
        assertTrue(viewModel.tutorCatalog.value.last().availableTimes.isEmpty())
        fillBooking(viewModel)
        viewModel.submitBooking(viewModel.tutorById("tutor-1")!!)
        runCurrent()
        assertTrue(viewModel.bookingUiState.value.reservationComplete)
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
        searchHistoryRepository = searchHistory,
        authRepository = auth,
        userRepository = users
    )
}

private fun profile(id: String, name: String) = UserProfile(
    id = id,
    name = name,
    email = "$id@example.com",
    role = UserRole.STUDENT,
    emailNotificationsEnabled = true,
    createdAt = Instant.EPOCH
)

private class FakeUserRepository(private val auth: FakeAuthRepository) : UserRepository {
    val profiles = mapOf(
        "student-1" to MutableStateFlow<UserProfile?>(profile("student-1", "Lucía")),
        "student-2" to MutableStateFlow<UserProfile?>(null)
    )
    var failure: Exception? = null
    var source: Flow<UserProfile?>? = null
    override fun observeCurrentUserProfile(): Flow<UserProfile?> {
        failure?.let { error -> return flow { throw error } }
        return source ?: profiles.getValue(requireNotNull(auth.userId.value))
    }
    override suspend fun updateProfile(profile: UserProfile) = Result.success(Unit)
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

private class FakeSearchHistoryRepository : SearchHistoryRepository {
    private val history = MutableStateFlow<List<SearchHistoryItem>>(emptyList())
    override fun observeHistory(): Flow<List<SearchHistoryItem>> = history
    override suspend fun saveSearch(query: String) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return
        val item = SearchHistoryItem(userId = "student-1", query = normalizedQuery, searchedAt = Instant.now())
        history.value = listOf(item) + history.value.filterNot { it.query == normalizedQuery }
    }
    override suspend fun clearHistory() { history.value = emptyList() }
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
