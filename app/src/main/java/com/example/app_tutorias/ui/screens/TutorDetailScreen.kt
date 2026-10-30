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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_tutorias.R
import com.example.app_tutorias.data.BookingFieldError
import com.example.app_tutorias.data.MAX_NOTES_LENGTH
import com.example.app_tutorias.data.MockData
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.ui.BookingUiState
import com.example.app_tutorias.ui.components.TutorAvatar
import com.example.app_tutorias.ui.theme.TutorLinkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorDetailScreen(
    tutor: Tutor,
    uiState: BookingUiState,
    onBookingStarted: () -> Unit,
    onTopicChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onTimeChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onViewCalendar: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val confirmationMessage = if (uiState.isPendingSync) {
        "Reserva pendiente de sincronización. Se enviará al recuperar la conexión; aún no está confirmada."
    } else {
        stringResource(R.string.booking_success)
    }

    LaunchedEffect(tutor.id) {
        onBookingStarted()
    }

    LaunchedEffect(uiState.reservationComplete) {
        if (uiState.reservationComplete) {
            snackbarHostState.showSnackbar(confirmationMessage)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = availability,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                    value = uiState.form.topic,
                    onValueChange = onTopicChanged,
                    label = stringResource(R.string.booking_topic),
                    placeholder = stringResource(R.string.booking_topic_hint),
                    error = if (uiState.topicTouched) {
                        uiState.validation.topicError.errorMessage()
                    } else {
                        null
                    }
                )
            }
            item {
                BookingTextField(
                    value = uiState.form.date,
                    onValueChange = onDateChanged,
                    label = stringResource(R.string.booking_date),
                    placeholder = stringResource(R.string.date_hint),
                    error = if (uiState.dateTouched) {
                        uiState.validation.dateError.errorMessage()
                    } else {
                        null
                    },
                    keyboardType = KeyboardType.Text
                )
            }
            item {
                BookingTextField(
                    value = uiState.form.time,
                    onValueChange = onTimeChanged,
                    label = stringResource(R.string.booking_time),
                    placeholder = stringResource(R.string.time_hint),
                    error = if (uiState.timeTouched) {
                        uiState.validation.timeError.errorMessage()
                    } else {
                        null
                    },
                    keyboardType = KeyboardType.Text
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.form.notes,
                    onValueChange = onNotesChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.booking_notes)) },
                    placeholder = { Text(stringResource(R.string.booking_notes_hint)) },
                    minLines = 3,
                    maxLines = 5,
                    isError = uiState.notesTouched &&
                        uiState.validation.notesError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (uiState.notesTouched) {
                                    uiState.validation.notesError.errorMessage().orEmpty()
                                } else {
                                    ""
                                }
                            )
                            Text("${uiState.form.notes.length}/$MAX_NOTES_LENGTH")
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                )
            }
            item {
                uiState.errorMessage?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error)
                }
                if (uiState.isPendingSync) {
                    Text("Pendiente de sincronización. Consulta el calendario para ver cuándo se confirma.")
                }
                Button(
                    onClick = onSubmit,
                    enabled = uiState.canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (uiState.isPendingSync) {
                                "Pendiente de sincronizar"
                            } else if (uiState.reservationComplete) {
                                stringResource(R.string.booking_confirmed)
                            } else {
                                stringResource(R.string.book_tutoring)
                            }
                        )
                    }
                }
            }
            if (uiState.reservationComplete) {
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = tutor.rating.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
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
            uiState = BookingUiState(),
            onBookingStarted = {},
            onTopicChanged = {},
            onDateChanged = {},
            onTimeChanged = {},
            onNotesChanged = {},
            onSubmit = {},
            onBack = {},
            onViewCalendar = {}
        )
    }
}
