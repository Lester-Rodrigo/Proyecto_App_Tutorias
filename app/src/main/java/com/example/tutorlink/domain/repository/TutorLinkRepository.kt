package com.example.tutorlink.domain.repository

import com.example.tutorlink.domain.model.TutorLinkData
import kotlinx.coroutines.flow.StateFlow

interface TutorLinkRepository {
    val data: StateFlow<TutorLinkData>
    fun retryLoading()
}
