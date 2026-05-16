package com.fieldflow.feature.auth.ui

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val message: String) : LoginUiState
    data class Error(val errorMessage: String) : LoginUiState
}

sealed interface LoginUiEvent {
    object NavigateToMap : LoginUiEvent
    data class ShowSnackbar(val message: String) : LoginUiEvent
}