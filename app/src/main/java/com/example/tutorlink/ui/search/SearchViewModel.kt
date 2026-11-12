package com.example.tutorlink.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorlink.data.filterTutors
import com.example.tutorlink.domain.model.SearchHistoryItem
import com.example.tutorlink.domain.repository.SearchHistoryRepository
import com.example.tutorlink.domain.repository.TutorLinkRepository
import com.example.tutorlink.ui.SearchUiState
import com.example.tutorlink.ui.UiLoadState
import com.example.tutorlink.ui.toUiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: TutorLinkRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState(loadState = UiLoadState.LOADING))
    val uiState = _uiState.asStateFlow()
    private var userId: String? = null
    private var history: List<SearchHistoryItem> = emptyList()

    init {
        viewModelScope.launch {
            searchHistoryRepository.observeHistory().catch { emit(emptyList()) }.collect {
                history = it
                refreshState()
            }
        }
        viewModelScope.launch {
            repository.data.collect { data ->
                if (userId != data.userId) {
                    userId = data.userId
                    _uiState.value = SearchUiState(loadState = UiLoadState.LOADING)
                }
                refreshState()
            }
        }
    }

    private fun refreshState() {
        val data = repository.data.value
        _uiState.update { state -> state.copy(
            categories = data.subjects.map { it.toUiModel() },
            filteredTutors = filterTutors(data.tutors.map { it.toUiModel(data.subjects) }, state.query, state.selectedCategoryId),
            history = history.filter { it.userId == data.userId }.map { it.query },
            loadState = UiLoadState.valueOf(data.catalogLoadState.name),
            errorMessage = data.catalogError
        ) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        refreshState()
    }
    fun clearSearchQuery() = onSearchQueryChanged("")
    fun onSearchSubmitted() = saveSearch(_uiState.value.query)
    fun onSearchHistorySelected(query: String) { onSearchQueryChanged(query); saveSearch(query) }
    fun onSearchCategorySelected(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = if (categoryId == it.selectedCategoryId) null else categoryId) }
        refreshState()
    }
    fun clearSearchHistory() { runHistoryWrite { searchHistoryRepository.clearHistory() } }
    private fun saveSearch(query: String) {
        if (query.isNotBlank()) runHistoryWrite { searchHistoryRepository.saveSearch(query) }
    }
    private fun runHistoryWrite(write: suspend () -> Unit) {
        viewModelScope.launch {
            try { write() }
            catch (exception: CancellationException) { throw exception }
            catch (_: Exception) { /* History must not prevent searching. */ }
        }
    }
    fun retryLoading() = repository.retryLoading()
}
