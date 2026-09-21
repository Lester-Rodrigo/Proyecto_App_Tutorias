package com.example.app_tutorias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_tutorias.R
import com.example.app_tutorias.data.MockData
import com.example.app_tutorias.data.TutoringSession
import com.example.app_tutorias.data.TutoringStatus
import com.example.app_tutorias.ui.components.EmptyState
import com.example.app_tutorias.ui.components.SessionCard
import com.example.app_tutorias.ui.theme.TutorLinkTextSecondary
import com.example.app_tutorias.ui.theme.TutorLinkTheme

private enum class SessionFilter {
    UPCOMING,
    COMPLETED
}

@Composable
fun CalendarScreen(sessions: List<TutoringSession>) {
    var selectedFilter by rememberSaveable { mutableStateOf(SessionFilter.UPCOMING) }
    val visibleSessions = when (selectedFilter) {
        SessionFilter.UPCOMING -> sessions
            .filter { it.status == TutoringStatus.UPCOMING }
            .sortedWith(compareBy(TutoringSession::date, TutoringSession::time))
        SessionFilter.COMPLETED -> sessions
            .filter { it.status == TutoringStatus.COMPLETED }
            .sortedByDescending { it.date }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.calendar_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Text(
            text = stringResource(R.string.calendar_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = TutorLinkTextSecondary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = selectedFilter == SessionFilter.UPCOMING,
                onClick = { selectedFilter = SessionFilter.UPCOMING },
                label = { Text(stringResource(R.string.upcoming_sessions)) }
            )
            FilterChip(
                selected = selectedFilter == SessionFilter.COMPLETED,
                onClick = { selectedFilter = SessionFilter.COMPLETED },
                label = { Text(stringResource(R.string.completed_sessions)) }
            )
        }

        if (visibleSessions.isEmpty()) {
            EmptyState(
                title = if (selectedFilter == SessionFilter.UPCOMING) {
                    stringResource(R.string.no_upcoming_title)
                } else {
                    stringResource(R.string.no_completed_title)
                },
                description = if (selectedFilter == SessionFilter.UPCOMING) {
                    stringResource(R.string.no_upcoming_description)
                } else {
                    stringResource(R.string.no_completed_description)
                },
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(visibleSessions, key = { it.id }) { session ->
                    SessionCard(session = session)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    TutorLinkTheme {
        CalendarScreen(sessions = MockData.initialSessions())
    }
}
