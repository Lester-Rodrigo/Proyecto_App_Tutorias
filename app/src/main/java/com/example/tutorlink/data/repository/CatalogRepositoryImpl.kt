package com.example.tutorlink.data.repository

import com.example.tutorlink.data.mapper.toDomain
import com.example.tutorlink.data.remote.firestore.CatalogRemoteDataSource
import com.example.tutorlink.domain.model.Subject
import com.example.tutorlink.domain.model.Tutor
import com.example.tutorlink.domain.repository.CatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CatalogRepositoryImpl(
    private val remoteDataSource: CatalogRemoteDataSource
) : CatalogRepository {

    override fun observeTutors(): Flow<List<Tutor>> =
        remoteDataSource.observeTutors().map { tutors -> tutors.map { it.toDomain() } }

    override fun observeTutor(tutorId: String): Flow<Tutor?> =
        remoteDataSource.observeTutor(tutorId).map { tutor -> tutor?.toDomain() }

    override fun observeSubjects(): Flow<List<Subject>> =
        remoteDataSource.observeSubjects().map { subjects -> subjects.map { it.toDomain() } }
}