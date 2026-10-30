package com.example.app_tutorias.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_tutorias.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()
    init {
        viewModelScope.launch {
            authRepository.currentUserId.collect { userId ->
                _uiState.update {
                    it.copy(
                        isAuthenticated = userId != null
                    )
                }
            }
        }
    }

    fun onNameChanged(value: String) {
        _uiState.update {
            it.copy(
                name = value,
                errorMessage = null
            )
        }
    }

    fun onEmailChanged(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                errorMessage = null
            )
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                errorMessage = null
            )
        }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                isRegisterMode = !it.isRegisterMode,
                errorMessage = null
            )
        }
    }

    fun submit() {
        val state = _uiState.value

        if (!state.canSubmit) return

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }
        viewModelScope.launch {
            val result = if (state.isRegisterMode) {
                authRepository.register(
                    name = state.name.trim(),
                    email = state.email.trim(),
                    password = state.password
                )
            } else {
                authRepository.signIn(
                    email = state.email.trim(),
                    password = state.password
                )
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = result
                        .exceptionOrNull()
                        ?.localizedMessage
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}