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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_tutorias.R
import com.example.app_tutorias.data.MockData
import com.example.app_tutorias.ui.CalendarUiState
import com.example.app_tutorias.ui.SessionFilter
import com.example.app_tutorias.ui.components.EmptyState
import com.example.app_tutorias.ui.components.SessionCard
import com.example.app_tutorias.ui.theme.TutorLinkTheme

@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    onFilterSelected: (SessionFilter) -> Unit
) {
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = uiState.selectedFilter == SessionFilter.UPCOMING,
                onClick = { onFilterSelected(SessionFilter.UPCOMING) },
                label = { Text(stringResource(R.string.upcoming_sessions)) }
            )
            FilterChip(
                selected = uiState.selectedFilter == SessionFilter.COMPLETED,
                onClick = { onFilterSelected(SessionFilter.COMPLETED) },
                label = { Text(stringResource(R.string.completed_sessions)) }
            )
        }

        if (uiState.visibleSessions.isEmpty()) {
            EmptyState(
                title = if (uiState.selectedFilter == SessionFilter.UPCOMING) {
                    stringResource(R.string.no_upcoming_title)
                } else {
                    stringResource(R.string.no_completed_title)
                },
                description = if (uiState.selectedFilter == SessionFilter.UPCOMING) {
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
                items(uiState.visibleSessions, key = { it.id }) { session ->
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
        CalendarScreen(
            uiState = CalendarUiState(
                visibleSessions = MockData.initialSessions()
            ),
            onFilterSelected = {}
        )
    }
}
