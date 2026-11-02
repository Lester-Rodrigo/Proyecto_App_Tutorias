package com.example.app_tutorias.di

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.app_tutorias.data.local.datastore.ThemeDataStore
import com.example.app_tutorias.data.local.room.TutoriasDatabase
import com.example.app_tutorias.data.remote.auth.AuthRemoteDataSource
import com.example.app_tutorias.data.remote.firestore.CatalogRemoteDataSource
import com.example.app_tutorias.data.remote.firestore.FavoritesRemoteDataSource
import com.example.app_tutorias.data.remote.firestore.SessionRemoteDataSource
import com.example.app_tutorias.data.remote.firestore.UserRemoteDataSource
import com.example.app_tutorias.data.repository.AuthRepositoryImpl
import com.example.app_tutorias.data.repository.CatalogRepositoryImpl
import com.example.app_tutorias.data.repository.FavoritesRepositoryImpl
import com.example.app_tutorias.data.repository.SearchHistoryRepositoryImpl
import com.example.app_tutorias.data.repository.SessionRepositoryImpl
import com.example.app_tutorias.data.repository.ThemeRepositoryImpl
import com.example.app_tutorias.data.repository.UserRepositoryImpl
import com.example.app_tutorias.domain.repository.AuthRepository
import com.example.app_tutorias.domain.repository.CatalogRepository
import com.example.app_tutorias.domain.repository.FavoritesRepository
import com.example.app_tutorias.domain.repository.SearchHistoryRepository
import com.example.app_tutorias.domain.repository.SessionRepository
import com.example.app_tutorias.domain.repository.ThemeRepository
import com.example.app_tutorias.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers

class AppContainer(context: Context ) {
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
        Room.databaseBuilder<TutoriasDatabase>(
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
}