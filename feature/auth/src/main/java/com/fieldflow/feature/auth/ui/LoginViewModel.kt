package com.fieldflow.feature.auth.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.feature.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authRepository: com.fieldflow.feature.auth.domain.repository.AuthRepository
) : ViewModel() {

    var emailInput by mutableStateOf("")
        private set

    var passwordInput by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            if (authRepository.isUserLoggedIn()) {
                _uiEvents.send(LoginUiEvent.NavigateToMap)
            }
        }
    }

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvents = Channel<LoginUiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    fun onEmailChanged(newValue: String) {
        emailInput = newValue
    }

    fun onPasswordChanged(newValue: String) {
        passwordInput = newValue
    }

    fun executeLoginSubmission() {
        // Defensive Check: Stop secondary execution threads if current process is active
        if (_uiState.value is LoginUiState.Loading) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            
            loginUseCase(emailInput.trim(), passwordInput)
                .onSuccess {
                    _uiState.value = LoginUiState.Success("Welcome back!")
                    _uiEvents.send(LoginUiEvent.NavigateToMap)
                }
                .onFailure { exception ->
                    val errorMsg = exception.localizedMessage ?: "Authentication failed"
                    _uiState.value = LoginUiState.Error(errorMsg)
                    _uiEvents.send(LoginUiEvent.ShowSnackbar(errorMsg))
                }
        }
    }
}