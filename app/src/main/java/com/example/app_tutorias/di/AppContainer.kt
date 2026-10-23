package com.example.app_tutorias.di

import com.example.app_tutorias.data.remote.auth.AuthRemoteDataSource
import com.example.app_tutorias.data.remote.firestore.CatalogRemoteDataSource
import com.example.app_tutorias.data.remote.firestore.SessionRemoteDataSource
import com.example.app_tutorias.data.remote.firestore.UserRemoteDataSource
import com.example.app_tutorias.data.repository.AuthRepositoryImpl
import com.example.app_tutorias.data.repository.CatalogRepositoryImpl
import com.example.app_tutorias.data.repository.SessionRepositoryImpl
import com.example.app_tutorias.data.repository.UserRepositoryImpl
import com.example.app_tutorias.domain.repository.AuthRepository
import com.example.app_tutorias.domain.repository.CatalogRepository
import com.example.app_tutorias.domain.repository.SessionRepository
import com.example.app_tutorias.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppContainer {
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
}