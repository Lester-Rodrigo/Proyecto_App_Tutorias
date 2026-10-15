package com.example.app_tutorias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.app_tutorias.data.SubjectCategory
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.data.filterTutors
import com.example.app_tutorias.ui.components.EmptyState
import com.example.app_tutorias.ui.components.TutorCard
import com.example.app_tutorias.ui.theme.TutorLinkTextSecondary
import com.example.app_tutorias.ui.theme.TutorLinkTheme

@Composable
fun SearchScreen(
    tutors: List<Tutor>,
    categories: List<SubjectCategory>,
    onTutorClick: (Tutor) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    val filteredTutors = filterTutors(tutors, query, selectedCategoryId)
    val hasActiveFilter = query.isNotBlank() || selectedCategoryId != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.search_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Text(
            text = stringResource(R.string.search_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = TutorLinkTextSecondary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null)
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(
                            Icons.Outlined.Clear,
                            contentDescription = stringResource(R.string.clear_search)
                        )
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategoryId == null,
                    onClick = { selectedCategoryId = null },
                    label = { Text(stringResource(R.string.all_subjects)) }
                )
            }
            items(categories, key = { it.id }) { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = {
                        selectedCategoryId = if (selectedCategoryId == category.id) {
                            null
                        } else {
                            category.id
                        }
                    },
                    label = { Text(category.name) }
                )
            }
        }

        if (filteredTutors.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.no_tutors_title),
                description = stringResource(R.string.no_tutors_description),
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = if (hasActiveFilter) {
                            stringResource(R.string.results_count, filteredTutors.size)
                        } else {
                            stringResource(R.string.available_tutors)
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(filteredTutors, key = { it.id }) { tutor ->
                    TutorCard(tutor = tutor, onClick = { onTutorClick(tutor) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    TutorLinkTheme {
        SearchScreen(
            tutors = MockData.tutors,
            categories = MockData.categories,
            onTutorClick = {}
        )
    }
}
