package com.example.tutorlink.di

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.tutorlink.data.local.datastore.ThemeDataStore
import com.example.tutorlink.data.local.room.TutorLinkDatabase
import com.example.tutorlink.data.remote.auth.AuthRemoteDataSource
import com.example.tutorlink.data.remote.firestore.CatalogRemoteDataSource
import com.example.tutorlink.data.remote.firestore.FavoritesRemoteDataSource
import com.example.tutorlink.data.remote.firestore.SessionRemoteDataSource
import com.example.tutorlink.data.remote.firestore.UserRemoteDataSource
import com.example.tutorlink.data.repository.AuthRepositoryImpl
import com.example.tutorlink.data.repository.CatalogRepositoryImpl
import com.example.tutorlink.data.repository.FavoritesRepositoryImpl
import com.example.tutorlink.data.repository.SearchHistoryRepositoryImpl
import com.example.tutorlink.data.repository.SessionRepositoryImpl
import com.example.tutorlink.data.repository.ThemeRepositoryImpl
import com.example.tutorlink.data.repository.UserRepositoryImpl
import com.example.tutorlink.domain.repository.AuthRepository
import com.example.tutorlink.domain.repository.CatalogRepository
import com.example.tutorlink.domain.repository.FavoritesRepository
import com.example.tutorlink.domain.repository.SearchHistoryRepository
import com.example.tutorlink.domain.repository.SessionRepository
import com.example.tutorlink.domain.repository.ThemeRepository
import com.example.tutorlink.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import com.example.tutorlink.data.repository.TutorLinkRepositoryImpl
import com.example.tutorlink.domain.repository.TutorLinkRepository

class AppContainer(context: Context ) {
    private val dataScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val authRemoteDataSource = AuthRemoteDataSource(firebaseAuth)
    private val userRemoteDataSource =
        UserRemoteDataSource(
            firestore = firebaseFirestore,
            auth = firebaseAuth
        )
    private val catalogRemoteDataSource = CatalogRemoteDataSource(firebaseFirestore)
    private val sessionRemoteDataSource =
        SessionRemoteDataSource(
            firestore = firebaseFirestore,
            auth = firebaseAuth
        )
    val authRepository: AuthRepository =
        AuthRepositoryImpl(
            authRemoteDataSource = authRemoteDataSource,
            userRemoteDataSource = userRemoteDataSource
        )
    val userRepository: UserRepository = UserRepositoryImpl(userRemoteDataSource)
    val catalogRepository: CatalogRepository = CatalogRepositoryImpl(catalogRemoteDataSource)
    val sessionRepository: SessionRepository = SessionRepositoryImpl(sessionRemoteDataSource)

    private val database =
        Room.databaseBuilder<TutorLinkDatabase>(
            context = context,
            name = "app_tutorias.db"
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

    private val favoritesRemoteDataSource =
        FavoritesRemoteDataSource(
            firestore = firebaseFirestore,
            auth = firebaseAuth
        )

    private val themeDataStore = ThemeDataStore(context)
    val favoritesRepository: FavoritesRepository = FavoritesRepositoryImpl(favoritesRemoteDataSource)
    val searchHistoryRepository: SearchHistoryRepository =
        SearchHistoryRepositoryImpl(
            dao = database.searchHistoryDao(),
            authRemoteDataSource = authRemoteDataSource
        )
    val themeRepository: ThemeRepository = ThemeRepositoryImpl(themeDataStore)
    val tutorLinkRepository: TutorLinkRepository = TutorLinkRepositoryImpl(
        catalogRepository = catalogRepository,
        sessionRepository = sessionRepository,
        authRepository = authRepository,
        userRepository = userRepository,
        scope = dataScope
    )
}