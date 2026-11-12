package com.example.tutorlink

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
import com.example.tutorlink.domain.repository.SearchHistoryRepository
import com.example.tutorlink.domain.repository.SessionRepository
import com.example.tutorlink.domain.repository.UserRepository
import com.example.tutorlink.ui.SessionFilter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.tutorlink.data.repository.TutorLinkRepositoryImpl
import com.example.tutorlink.domain.repository.TutorLinkRepository
import com.example.tutorlink.ui.ScreenViewModelFactory
import com.example.tutorlink.ui.home.HomeViewModel
import com.example.tutorlink.ui.search.SearchViewModel
import com.example.tutorlink.ui.calendar.CalendarViewModel
import com.example.tutorlink.ui.detail.TutorDetailViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import com.example.tutorlink.ui.UiLoadState
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.emitAll
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
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreFlowViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repositoryScope = CoroutineScope(SupervisorJob() + dispatcher)
    private val viewModelStore = ViewModelStore()
    private val auth = FakeAuthRepository()
    private val users = FakeUserRepository(auth)
    private val catalog = FakeCatalogRepository()
    private val sessions = FakeSessionRepository()
    private val searchHistory = FakeSearchHistoryRepository()

    @Before
    fun setUp() { Dispatchers.setMain(dispatcher) }

    @After
    fun tearDown() { viewModelStore.clear(); repositoryScope.cancel(); Dispatchers.resetMain() }

    @Test
    fun `catalog comes from repository and booking appears through session stream`() = runTest(dispatcher) {
        val screens = createScreens()
        runCurrent()
        assertEquals(listOf("Tutor real"), screens.search.uiState.value.filteredTutors.map { it.name })
        assertEquals(UiLoadState.CONTENT, screens.home.uiState.value.loadState)
        fillBooking(screens.detail)
        val tutor = screens.search.uiState.value.filteredTutors.single()
        screens.detail.submitBooking(tutor)
        screens.detail.submitBooking(tutor)
        runCurrent()
        assertEquals(1, sessions.writes.size)
        assertEquals("student-1", sessions.writes.single().studentId)
        assertEquals("math", sessions.writes.single().subjectId)
        assertTrue(screens.detail.bookingUiState.value.reservationComplete)
        assertEquals(1, screens.calendar.uiState.value.visibleSessions.size)
        screens.calendar.onSessionFilterSelected(SessionFilter.COMPLETED)
        assertEquals(UiLoadState.EMPTY, screens.calendar.uiState.value.loadState)
    }

    @Test
    fun `failed write shows error and never confirms booking`() = runTest(dispatcher) {
        sessions.failure = IllegalStateException("permission denied")
        val screens = createScreens()
        runCurrent()
        fillBooking(screens.detail)
        screens.detail.submitBooking(screens.search.uiState.value.filteredTutors.single())
        runCurrent()
        assertFalse(screens.detail.bookingUiState.value.isSubmitting)
        assertFalse(screens.detail.bookingUiState.value.reservationComplete)
        assertNotNull(screens.detail.bookingUiState.value.errorMessage)
        assertTrue(screens.calendar.uiState.value.visibleSessions.isEmpty())
    }

    @Test
    fun `pending write is distinguished from server confirmation`() = runTest(dispatcher) {
        sessions.pending = true
        val screens = createScreens()
        runCurrent()
        fillBooking(screens.detail)
        screens.detail.submitBooking(screens.search.uiState.value.filteredTutors.single())
        runCurrent()
        assertTrue(screens.detail.bookingUiState.value.isPendingSync)
        assertTrue(screens.calendar.uiState.value.visibleSessions.single().isPendingSync)
        sessions.sessionFlow.value = sessions.sessionFlow.value.map { it.copy(isPendingSync = false) }
        runCurrent()
        assertFalse(screens.detail.bookingUiState.value.isPendingSync)
        assertFalse(screens.calendar.uiState.value.visibleSessions.single().isPendingSync)
    }

    @Test
    fun `missing first snapshot stops loading and can recover`() = runTest(dispatcher) {
        catalog.tutorFlow = MutableStateFlow<List<Tutor>?>(null).let { source ->
            flow { source.collect { value -> if (value != null) emit(value) } }
        }
        val screens = createScreens()
        runCurrent()
        assertEquals(UiLoadState.LOADING, screens.home.uiState.value.loadState)
        advanceTimeBy(12_001)
        runCurrent()
        assertEquals(UiLoadState.ERROR, screens.home.uiState.value.loadState)
        catalog.tutorFlow = flowOf(listOf(catalog.tutor))
        screens.home.retryLoading()
        runCurrent()
        assertEquals(UiLoadState.CONTENT, screens.home.uiState.value.loadState)
    }

    @Test
    fun `catalog error is visible and signing out clears previous account data`() = runTest(dispatcher) {
        val screens = createScreens()
        runCurrent()
        fillBooking(screens.detail)
        screens.detail.submitBooking(screens.search.uiState.value.filteredTutors.single())
        runCurrent()
        auth.userId.value = null
        runCurrent()
        assertTrue(screens.calendar.uiState.value.visibleSessions.isEmpty())
        assertTrue(screens.search.uiState.value.filteredTutors.isEmpty())
        catalog.tutorFlow = flow { throw IllegalStateException("permission denied") }
        auth.userId.value = "student-2"
        runCurrent()
        assertEquals(UiLoadState.ERROR, screens.search.uiState.value.loadState)
    }

    @Test
    fun `submitted searches are saved in history and can be reused`() = runTest(dispatcher) {
        val screens = createScreens()
        runCurrent()
        screens.search.onSearchQueryChanged("Álgebra")
        screens.search.onSearchSubmitted()
        screens.search.onSearchQueryChanged("Kotlin")
        screens.search.onSearchSubmitted()
        screens.search.onSearchQueryChanged("   ")
        screens.search.onSearchSubmitted()
        runCurrent()
        assertEquals(listOf("Kotlin", "Álgebra"), screens.search.uiState.value.history)
        screens.search.onSearchHistorySelected("Álgebra")
        runCurrent()
        assertEquals("Álgebra", screens.search.uiState.value.query)
        assertEquals(listOf("Álgebra", "Kotlin"), screens.search.uiState.value.history)
        screens.search.clearSearchHistory()
        runCurrent()
        assertTrue(screens.search.uiState.value.history.isEmpty())
    }

    @Test
    fun `history stays visible when the catalog cannot load`() = runTest(dispatcher) {
        searchHistory.saveSearch("Física")
        catalog.tutorFlow = flow { throw IllegalStateException("offline") }
        val screens = createScreens()
        runCurrent()
        assertEquals(UiLoadState.ERROR, screens.search.uiState.value.loadState)
        assertEquals(listOf("Física"), screens.search.uiState.value.history)
    }

    @Test
    fun `greeting follows profile updates and clears when changing accounts`() = runTest(dispatcher) {
        val screens = createScreens()
        runCurrent()
        assertEquals("Lucía", screens.home.uiState.value.userName)
        users.profiles.getValue("student-1").value = profile("student-1", "  Marta  ")
        runCurrent()
        assertEquals("Marta", screens.home.uiState.value.userName)

        auth.userId.value = "student-2"
        runCurrent()
        assertNull(screens.home.uiState.value.userName)
        // A late update from the previous account must not reach the current screen.
        users.profiles.getValue("student-1").value = profile("student-1", "Nombre anterior")
        users.profiles.getValue("student-2").value = profile("student-2", "Diego")
        runCurrent()
        assertEquals("Diego", screens.home.uiState.value.userName)
        auth.userId.value = null
        runCurrent()
        assertNull(screens.home.uiState.value.userName)
    }

    @Test
    fun `missing blank or mismatched profile never supplies a greeting name`() = runTest(dispatcher) {
        users.profiles.getValue("student-1").value = null
        val screens = createScreens()
        runCurrent()
        assertNull(screens.home.uiState.value.userName)
        users.profiles.getValue("student-1").value = profile("student-1", "   ")
        runCurrent()
        assertNull(screens.home.uiState.value.userName)
        users.profiles.getValue("student-1").value = profile("student-2", "Otro usuario")
        runCurrent()
        assertNull(screens.home.uiState.value.userName)
        assertEquals(UiLoadState.CONTENT, screens.home.uiState.value.loadState)
    }

    @Test
    fun `profile error does not block catalog or booking and retry restores the name`() = runTest(dispatcher) {
        users.failure = IllegalStateException("offline without cached profile")
        val screens = createScreens()
        runCurrent()
        assertNull(screens.home.uiState.value.userName)
        assertEquals(UiLoadState.CONTENT, screens.home.uiState.value.loadState)
        fillBooking(screens.detail)
        screens.detail.submitBooking(screens.search.uiState.value.filteredTutors.single())
        runCurrent()
        assertTrue(screens.detail.bookingUiState.value.reservationComplete)
        users.failure = null
        screens.home.retryLoading()
        runCurrent()
        assertEquals("Lucía", screens.home.uiState.value.userName)
    }

    @Test
    fun `profile deadline keeps catalog usable and a later cached snapshot updates the name`() = runTest(dispatcher) {
        val profileSource = MutableStateFlow<UserProfile?>(null)
        users.source = flow { profileSource.collect { if (it != null) emit(it) } }
        val screens = createScreens()
        runCurrent()
        advanceTimeBy(12_001)
        runCurrent()
        assertNull(screens.home.uiState.value.userName)
        assertEquals(UiLoadState.CONTENT, screens.home.uiState.value.loadState)
        profileSource.value = profile("student-1", "Nombre en caché")
        runCurrent()
        assertEquals("Nombre en caché", screens.home.uiState.value.userName)
    }

    @Test
    fun `detail availability follows repository snapshots without fabricated times`() = runTest(dispatcher) {
        val recommended = (1..3).map { catalog.tutor.copy(id = "recommended-$it", name = "Recomendado $it") }
        val tutorSource = MutableStateFlow(recommended + catalog.tutor.copy(availability = listOf(
            AvailabilitySlot("monday", 1, "15:00", "18:00"),
            AvailabilitySlot("wednesday", 3, "09:00", "11:00")
        )))
        catalog.tutorFlow = tutorSource
        val screens = createScreens()
        runCurrent()
        screens.search.onSearchQueryChanged("Recomendado")
        val homeBefore = screens.home.uiState.value
        val searchBefore = screens.search.uiState.value
        assertEquals(listOf("lunes · 15:00–18:00", "miércoles · 09:00–11:00"),
            screens.detail.uiState.value.tutors.last().availableTimes)
        tutorSource.value = recommended + catalog.tutor.copy(availability = listOf(
            AvailabilitySlot("tuesday", 2, "10:00", "12:00")
        ))
        runCurrent()
        assertEquals(listOf("martes · 10:00–12:00"), screens.detail.uiState.value.tutors.last().availableTimes)
        // Detail still updates when its tutor is outside recommendations and search results.
        assertEquals(homeBefore, screens.home.uiState.value)
        assertEquals(searchBefore, screens.search.uiState.value)
        tutorSource.value = recommended + catalog.tutor
        runCurrent()
        assertTrue(screens.detail.uiState.value.tutors.last().availableTimes.isEmpty())
        fillBooking(screens.detail)
        screens.detail.submitBooking(screens.detail.tutorById("tutor-1")!!)
        runCurrent()
        assertTrue(screens.detail.bookingUiState.value.reservationComplete)
    }

    @Test
    fun `four screens share authenticated reads and retry replaces the listeners`() = runTest(dispatcher) {
        val screens = createScreens()
        runCurrent()
        assertEquals(1, catalog.tutorSubscriptions)
        assertEquals(1, catalog.subjectSubscriptions)
        assertEquals(1, sessions.subscriptions)
        assertEquals(1, users.subscriptions)
        assertEquals(1, catalog.activeTutors)
        assertEquals(1, sessions.activeSubscriptions)
        screens.search.retryLoading()
        runCurrent()
        assertEquals(2, catalog.tutorSubscriptions)
        assertEquals(2, catalog.subjectSubscriptions)
        assertEquals(2, sessions.subscriptions)
        assertEquals(2, users.subscriptions)
        assertEquals(1, catalog.activeTutors)
        assertEquals(1, sessions.activeSubscriptions)
        assertEquals(UiLoadState.CONTENT, screens.detail.uiState.value.loadState)
    }

    @Test
    fun `reusing the owner retains search filter calendar selection and booking form`() = runTest(dispatcher) {
        val screens = createScreens()
        runCurrent()
        screens.search.onSearchQueryChanged("Tutor")
        screens.search.onSearchCategorySelected("math")
        screens.calendar.onSessionFilterSelected(SessionFilter.COMPLETED)
        fillBooking(screens.detail)
        screens.detail.onBookingNotesChanged("Repasar ecuaciones")
        val restored = createScreens(screens.repository)
        assertSame(screens.home, restored.home)
        assertSame(screens.search, restored.search)
        assertSame(screens.calendar, restored.calendar)
        assertSame(screens.detail, restored.detail)
        restored.home.retryLoading()
        runCurrent()
        assertEquals("Tutor", restored.search.uiState.value.query)
        assertEquals("math", restored.search.uiState.value.selectedCategoryId)
        assertEquals(SessionFilter.COMPLETED, restored.calendar.uiState.value.selectedFilter)
        assertEquals("Repasar ecuaciones", restored.detail.bookingUiState.value.form.notes)
        restored.detail.beginBooking("tutor-1", "detail-1")
        assertEquals("Álgebra", restored.detail.bookingUiState.value.form.topic)
        restored.detail.beginBooking("tutor-1", "detail-2")
        assertEquals("", restored.detail.bookingUiState.value.form.topic)
    }

    @Test
    fun `changing accounts cancels an in flight booking and clears screen state`() = runTest(dispatcher) {
        sessions.waitForWrite = CompletableDeferred()
        val screens = createScreens()
        runCurrent()
        screens.search.onSearchQueryChanged("Tutor")
        screens.calendar.onSessionFilterSelected(SessionFilter.COMPLETED)
        fillBooking(screens.detail)
        screens.detail.submitBooking(screens.detail.tutorById("tutor-1")!!)
        runCurrent()
        assertTrue(screens.detail.bookingUiState.value.isSubmitting)
        auth.userId.value = "student-2"
        runCurrent()
        sessions.waitForWrite!!.complete(Unit)
        runCurrent()
        assertTrue(sessions.writes.isEmpty())
        assertFalse(screens.detail.bookingUiState.value.reservationComplete)
        assertFalse(screens.detail.bookingUiState.value.isSubmitting)
        assertEquals("", screens.detail.bookingUiState.value.form.topic)
        assertEquals("", screens.search.uiState.value.query)
        assertEquals(SessionFilter.UPCOMING, screens.calendar.uiState.value.selectedFilter)
        assertNull(screens.home.uiState.value.userName)
    }

    @Test
    fun `shared reads stop after owners clear and restart without stale account data`() = runTest(dispatcher) {
        val screens = createScreens()
        runCurrent()
        assertEquals("Lucía", screens.home.uiState.value.userName)
        viewModelStore.clear()
        runCurrent()
        advanceTimeBy(5_001)
        runCurrent()
        assertEquals(0, catalog.activeTutors)
        assertEquals(0, sessions.activeSubscriptions)
        assertNull(screens.repository.data.value.userId)
        auth.userId.value = "student-2"
        val reopened = createScreens(screens.repository)
        runCurrent()
        assertNull(reopened.home.uiState.value.userName)
        assertEquals(2, catalog.tutorSubscriptions)
        assertEquals(1, catalog.activeTutors)
        assertEquals(1, sessions.activeSubscriptions)
    }

    private fun fillBooking(viewModel: TutorDetailViewModel) {
        viewModel.beginBooking("tutor-1", "detail-1")
        viewModel.onBookingTopicChanged("Álgebra")
        viewModel.onBookingDateChanged(LocalDate.now().plusDays(1).toString())
        viewModel.onBookingTimeChanged("16:30")
    }

    private fun createScreens(
        repository: TutorLinkRepository = TutorLinkRepositoryImpl(catalog, sessions, auth, users, repositoryScope)
    ): Screens {
        val provider = ViewModelProvider(viewModelStore, ScreenViewModelFactory(repository, sessions, searchHistory))
        return Screens(provider[HomeViewModel::class.java], provider[SearchViewModel::class.java],
            provider[CalendarViewModel::class.java], provider[TutorDetailViewModel::class.java], repository)
    }
}

private data class Screens(
    val home: HomeViewModel,
    val search: SearchViewModel,
    val calendar: CalendarViewModel,
    val detail: TutorDetailViewModel,
    val repository: TutorLinkRepository
)

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
    var subscriptions = 0
    override fun observeCurrentUserProfile(): Flow<UserProfile?> {
        subscriptions++
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
    var tutorFlow: Flow<List<Tutor>> = MutableStateFlow(listOf(tutor))
    var tutorSubscriptions = 0
    var subjectSubscriptions = 0
    var activeTutors = 0
    override fun observeTutors(): Flow<List<Tutor>> = flow {
        tutorSubscriptions++
        activeTutors++
        try { emitAll(tutorFlow) } finally { activeTutors-- }
    }
    override fun observeTutor(tutorId: String): Flow<Tutor?> = flowOf(tutor)
    override fun observeSubjects(): Flow<List<Subject>> = flow {
        subjectSubscriptions++
        emit(listOf(Subject("math", "Matemáticas", "math", "📐")))
    }
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
    var subscriptions = 0
    var activeSubscriptions = 0
    var waitForWrite: CompletableDeferred<Unit>? = null
    override fun observeMySessions(): Flow<List<TutoringSession>> = flow {
        subscriptions++
        activeSubscriptions++
        try { emitAll(sessionFlow) } finally { activeSubscriptions-- }
    }
    override suspend fun createSession(session: TutoringSession): Result<SessionWriteResult> {
        waitForWrite?.await()
        failure?.let { return Result.failure(it) }
        writes.add(session)
        sessionFlow.value = sessionFlow.value + session.copy(isPendingSync = pending)
        return Result.success(SessionWriteResult(session.id, pending))
    }
    override suspend fun updateSessionStatus(sessionId: String, status: TutoringStatus) = Result.success(Unit)
    override suspend fun deleteSession(sessionId: String) = Result.success(Unit)
}
