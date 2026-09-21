package com.example.app_tutorias.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_tutorias.R
import com.example.app_tutorias.data.MockData
import com.example.app_tutorias.data.SubjectCategory
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.data.TutoringSession
import com.example.app_tutorias.ui.components.EmptyState
import com.example.app_tutorias.ui.components.SessionCard
import com.example.app_tutorias.ui.components.TutorCard
import com.example.app_tutorias.ui.theme.TutorFavoriteBackground
import com.example.app_tutorias.ui.theme.TutorLinkGreenActive
import com.example.app_tutorias.ui.theme.TutorLinkTextSecondary
import com.example.app_tutorias.ui.theme.TutorLinkTheme

@Composable
fun HomeScreen(
    categories: List<SubjectCategory>,
    recommendedTutors: List<Tutor>,
    nextSession: TutoringSession?,
    onSearchClick: () -> Unit,
    onTutorClick: (Tutor) -> Unit,
    onCalendarClick: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.home_greeting),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TutorLinkTextSecondary
                )
            }
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSearchClick),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = TutorLinkGreenActive
                    )
                    Text(
                        text = stringResource(R.string.search_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TutorLinkTextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = TutorLinkGreenActive
                    )
                }
            }
        }
        item { SectionTitle(stringResource(R.string.popular_subjects)) }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(categories, key = { it.id }) { category ->
                    Surface(
                        modifier = Modifier.clickable(onClick = onSearchClick),
                        shape = CircleShape,
                        color = TutorFavoriteBackground
                    ) {
                        Text(
                            text = "${category.emoji}  ${category.name}",
                            style = MaterialTheme.typography.labelLarge,
                            color = TutorLinkGreenActive,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
        item { SectionTitle(stringResource(R.string.recommended_tutors)) }
        items(recommendedTutors, key = { it.id }) { tutor ->
            TutorCard(tutor = tutor, onClick = { onTutorClick(tutor) })
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle(stringResource(R.string.next_tutoring))
                Text(
                    text = stringResource(R.string.view_calendar),
                    style = MaterialTheme.typography.labelLarge,
                    color = TutorLinkGreenActive,
                    modifier = Modifier.clickable(onClick = onCalendarClick)
                )
            }
        }
        item {
            if (nextSession == null) {
                EmptyState(
                    title = stringResource(R.string.no_upcoming_title),
                    description = stringResource(R.string.no_upcoming_description)
                )
            } else {
                Box(modifier = Modifier.clickable(onClick = onCalendarClick)) {
                    SessionCard(session = nextSession)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleLarge)
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TutorLinkTheme {
        HomeScreen(
            categories = MockData.categories,
            recommendedTutors = MockData.tutors.take(2),
            nextSession = MockData.initialSessions().first(),
            onSearchClick = {},
            onTutorClick = {},
            onCalendarClick = {}
        )
    }
}
