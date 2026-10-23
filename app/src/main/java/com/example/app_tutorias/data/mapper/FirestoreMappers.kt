package com.example.app_tutorias.data.mapper

import com.example.app_tutorias.data.remote.firestore.AvailabilitySlotDto
import com.example.app_tutorias.data.remote.firestore.FavoriteTutorDto
import com.example.app_tutorias.data.remote.firestore.SessionDto
import com.example.app_tutorias.data.remote.firestore.SubjectDto
import com.example.app_tutorias.data.remote.firestore.TutorDto
import com.example.app_tutorias.data.remote.firestore.UserDto
import com.example.app_tutorias.domain.model.AvailabilitySlot
import com.example.app_tutorias.domain.model.FavoriteTutor
import com.example.app_tutorias.domain.model.Subject
import com.example.app_tutorias.domain.model.Tutor
import com.example.app_tutorias.domain.model.TutoringSession
import com.example.app_tutorias.domain.model.TutoringStatus
import com.example.app_tutorias.domain.model.UserProfile
import com.example.app_tutorias.domain.model.UserRole
import com.google.firebase.Timestamp
import java.util.Date

fun UserDto.toDomain(): UserProfile = UserProfile(
    id = id,
    name = name,
    email = email,
    role = runCatching {
        UserRole.valueOf(role)
    }.getOrDefault(UserRole.STUDENT),
    emailNotificationsEnabled = emailNotificationsEnabled,
    createdAt = createdAt.toDate().toInstant()
)

fun UserProfile.toDto(): UserDto = UserDto(
    id = id,
    name = name,
    email = email,
    role = role.name,
    emailNotificationsEnabled = emailNotificationsEnabled,
    createdAt = Timestamp(Date.from(createdAt))
)

fun SubjectDto.toDomain(): Subject = Subject(
    id = id,
    name = name,
    category = category,
    emoji = emoji
)

fun Subject.toDto(): SubjectDto = SubjectDto(
    id = id,
    name = name,
    category = category,
    emoji = emoji
)

fun AvailabilitySlotDto.toDomain(): AvailabilitySlot =
    AvailabilitySlot(
        id = id,
        dayOfWeek = dayOfWeek,
        startTime = startTime,
        endTime = endTime
    )

fun AvailabilitySlot.toDto(): AvailabilitySlotDto =
    AvailabilitySlotDto(
        id = id,
        dayOfWeek = dayOfWeek,
        startTime = startTime,
        endTime = endTime
    )

fun TutorDto.toDomain(): Tutor = Tutor(
    id = id,
    name = name,
    initials = initials,
    description = description,
    subjectIds = subjectIds,
    pricePerHour = pricePerHour,
    averageRating = averageRating,
    ratingCount = ratingCount,
    availability = availability.map { it.toDomain() }
)

fun Tutor.toDto(): TutorDto = TutorDto(
    id = id,
    name = name,
    initials = initials,
    description = description,
    subjectIds = subjectIds,
    pricePerHour = pricePerHour,
    averageRating = averageRating,
    ratingCount = ratingCount,
    availability = availability.map { it.toDto() }
)

fun SessionDto.toDomain(): TutoringSession =
    TutoringSession(
        id = id,
        studentId = studentId,
        studentName = studentName,
        tutorId = tutorId,
        tutorName = tutorName,
        subjectId = subjectId,
        subjectName = subjectName,
        startAt = startAt.toDate().toInstant(),
        endAt = endAt.toDate().toInstant(),
        notes = notes,
        status = runCatching {
            TutoringStatus.valueOf(status)
        }.getOrDefault(TutoringStatus.UPCOMING),
        createdAt = createdAt.toDate().toInstant()
    )

fun TutoringSession.toDto(): SessionDto =
    SessionDto(
        id = id,
        studentId = studentId,
        studentName = studentName,
        tutorId = tutorId,
        tutorName = tutorName,
        subjectId = subjectId,
        subjectName = subjectName,
        startAt = Timestamp(Date.from(startAt)),
        endAt = Timestamp(Date.from(endAt)),
        notes = notes,
        status = status.name,
        createdAt = Timestamp(Date.from(createdAt))
    )

fun FavoriteTutorDto.toDomain(): FavoriteTutor =
    FavoriteTutor(
        tutorId = tutorId,
        tutorName = tutorName,
        addedAt = addedAt.toDate().toInstant()
    )

fun FavoriteTutor.toDto(): FavoriteTutorDto =
    FavoriteTutorDto(
        tutorId = tutorId,
        tutorName = tutorName,
        addedAt = Timestamp(Date.from(addedAt))
    )