package com.example.app_tutorias.ui.login

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
) {
    val isNameValid: Boolean
        get() = !isRegisterMode || name.trim().length >= 2
    val isEmailValid: Boolean
        get() = email.trim().contains("@") &&
                email.trim().contains(".")
    val isPasswordValid: Boolean
        get() = password.length >= 6
    val canSubmit: Boolean
        get() = isNameValid &&
                isEmailValid &&
                isPasswordValid &&
                !isLoading
}