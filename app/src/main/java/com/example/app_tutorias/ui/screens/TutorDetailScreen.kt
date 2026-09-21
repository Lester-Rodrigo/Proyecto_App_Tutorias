package com.example.app_tutorias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_tutorias.R
import com.example.app_tutorias.data.BookingFieldError
import com.example.app_tutorias.data.BookingFormData
import com.example.app_tutorias.data.MAX_NOTES_LENGTH
import com.example.app_tutorias.data.MockData
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.data.TutoringSession
import com.example.app_tutorias.data.TutoringStatus
import com.example.app_tutorias.data.validateBookingForm
import com.example.app_tutorias.ui.components.TutorAvatar
import com.example.app_tutorias.ui.theme.TutorFavoriteBackground
import com.example.app_tutorias.ui.theme.TutorLinkGreenActive
import com.example.app_tutorias.ui.theme.TutorLinkTextSecondary
import com.example.app_tutorias.ui.theme.TutorLinkTheme
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorDetailScreen(
    tutor: Tutor,
    onBack: () -> Unit,
    onReservationConfirmed: (TutoringSession) -> Unit,
    onViewCalendar: () -> Unit
) {
    var topic by rememberSaveable(tutor.id) { mutableStateOf("") }
    var date by rememberSaveable(tutor.id) { mutableStateOf("") }
    var time by rememberSaveable(tutor.id) { mutableStateOf("") }
    var notes by rememberSaveable(tutor.id) { mutableStateOf("") }
    var topicTouched by rememberSaveable(tutor.id) { mutableStateOf(false) }
    var dateTouched by rememberSaveable(tutor.id) { mutableStateOf(false) }
    var timeTouched by rememberSaveable(tutor.id) { mutableStateOf(false) }
    var notesTouched by rememberSaveable(tutor.id) { mutableStateOf(false) }
    var isSubmitting by rememberSaveable(tutor.id) { mutableStateOf(false) }
    var reservationComplete by rememberSaveable(tutor.id) { mutableStateOf(false) }

    val form = BookingFormData(topic = topic, date = date, time = time, notes = notes)
    val validation = validateBookingForm(form)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val confirmationMessage = stringResource(R.string.booking_success)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tutor_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { TutorHeader(tutor) }
            item {
                Text(
                    text = tutor.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TutorLinkTextSecondary
                )
            }
            item {
                Text(
                    text = stringResource(R.string.available_times),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        listOf(tutor.availability, "Viernes · 11:00 a. m.", "Sábado · 9:00 a. m.")
                    ) { availability ->
                        Surface(
                            color = TutorFavoriteBackground,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = availability,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TutorLinkGreenActive,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            item {
                Text(
                    text = stringResource(R.string.booking_form_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            item {
                BookingTextField(
                    value = topic,
                    onValueChange = {
                        topic = it
                        topicTouched = true
                    },
                    label = stringResource(R.string.booking_topic),
                    placeholder = stringResource(R.string.booking_topic_hint),
                    error = if (topicTouched) validation.topicError.errorMessage() else null
                )
            }
            item {
                BookingTextField(
                    value = date,
                    onValueChange = {
                        date = it
                        dateTouched = true
                    },
                    label = stringResource(R.string.booking_date),
                    placeholder = stringResource(R.string.date_hint),
                    error = if (dateTouched) validation.dateError.errorMessage() else null,
                    keyboardType = KeyboardType.Number
                )
            }
            item {
                BookingTextField(
                    value = time,
                    onValueChange = {
                        time = it
                        timeTouched = true
                    },
                    label = stringResource(R.string.booking_time),
                    placeholder = stringResource(R.string.time_hint),
                    error = if (timeTouched) validation.timeError.errorMessage() else null,
                    keyboardType = KeyboardType.Number
                )
            }
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = {
                        notes = it
                        notesTouched = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.booking_notes)) },
                    placeholder = { Text(stringResource(R.string.booking_notes_hint)) },
                    minLines = 3,
                    maxLines = 5,
                    isError = notesTouched && validation.notesError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (notesTouched) {
                                    validation.notesError.errorMessage().orEmpty()
                                } else {
                                    ""
                                }
                            )
                            Text("${notes.length}/$MAX_NOTES_LENGTH")
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                )
            }
            item {
                Button(
                    onClick = {
                        if (!validation.isValid || isSubmitting || reservationComplete) return@Button
                        isSubmitting = true
                        coroutineScope.launch {
                            delay(700)
                            val session = TutoringSession(
                                id = "session-${tutor.id}-$date-$time",
                                tutorId = tutor.id,
                                tutorName = tutor.name,
                                subject = topic.trim(),
                                date = LocalDate.parse(date),
                                time = LocalTime.parse(time),
                                notes = notes.trim(),
                                status = TutoringStatus.UPCOMING
                            )
                            onReservationConfirmed(session)
                            isSubmitting = false
                            reservationComplete = true
                            snackbarHostState.showSnackbar(confirmationMessage)
                        }
                    },
                    enabled = validation.isValid && !isSubmitting && !reservationComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (reservationComplete) {
                                stringResource(R.string.booking_confirmed)
                            } else {
                                stringResource(R.string.book_tutoring)
                            }
                        )
                    }
                }
            }
            if (reservationComplete) {
                item {
                    OutlinedButton(
                        onClick = onViewCalendar,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.view_calendar))
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun TutorHeader(tutor: Tutor) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TutorAvatar(initials = tutor.initials, modifier = Modifier.size(76.dp))
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(text = tutor.name, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = tutor.specialty,
                style = MaterialTheme.typography.bodyLarge,
                color = TutorLinkTextSecondary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = TutorLinkGreenActive,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = tutor.rating.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = TutorLinkGreenActive
                    )
                }
                Text(
                    text = "Q${tutor.pricePerHour} / hora",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun BookingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error) }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun BookingFieldError?.errorMessage(): String? = when (this) {
    BookingFieldError.REQUIRED -> stringResource(R.string.error_required)
    BookingFieldError.TOO_SHORT -> stringResource(R.string.error_topic_short)
    BookingFieldError.INVALID_DATE -> stringResource(R.string.error_invalid_date)
    BookingFieldError.PAST_DATE -> stringResource(R.string.error_past_date)
    BookingFieldError.INVALID_TIME -> stringResource(R.string.error_invalid_time)
    BookingFieldError.NOTES_TOO_LONG -> stringResource(R.string.error_notes_too_long)
    null -> null
}

@Preview(showBackground = true)
@Composable
private fun TutorDetailPreview() {
    TutorLinkTheme {
        TutorDetailScreen(
            tutor = MockData.tutors.first(),
            onBack = {},
            onReservationConfirmed = {},
            onViewCalendar = {}
        )
    }
}
