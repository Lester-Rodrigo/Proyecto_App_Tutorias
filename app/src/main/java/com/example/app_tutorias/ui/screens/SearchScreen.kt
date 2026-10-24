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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_tutorias.R
import com.example.app_tutorias.data.MockData
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.ui.SearchUiState
import com.example.app_tutorias.ui.components.EmptyState
import com.example.app_tutorias.ui.components.TutorCard
import com.example.app_tutorias.ui.theme.TutorLinkTheme

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onQueryChanged: (String) -> Unit,
    onClearQuery: () -> Unit,
    onCategorySelected: (String?) -> Unit,
    onTutorClick: (Tutor) -> Unit
) {
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null)
            },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = onClearQuery) {
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
                    selected = uiState.selectedCategoryId == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text(stringResource(R.string.all_subjects)) }
                )
            }
            items(uiState.categories, key = { it.id }) { category ->
                FilterChip(
                    selected = uiState.selectedCategoryId == category.id,
                    onClick = { onCategorySelected(category.id) },
                    label = { Text(category.name) }
                )
            }
        }

        if (uiState.filteredTutors.isEmpty()) {
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
                        text = if (uiState.hasActiveFilter) {
                            stringResource(R.string.results_count, uiState.filteredTutors.size)
                        } else {
                            stringResource(R.string.available_tutors)
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(uiState.filteredTutors, key = { it.id }) { tutor ->
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
            uiState = SearchUiState(
                categories = MockData.categories,
                filteredTutors = MockData.tutors
            ),
            onQueryChanged = {},
            onClearQuery = {},
            onCategorySelected = {},
            onTutorClick = {}
        )
    }
}
