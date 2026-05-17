// feature/business/src/main/java/com/fieldflow/feature/business/ui/detail/BusinessDetailViewModel.kt
package com.fieldflow.feature.business.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.feature.business.data.repository.BusinessRepository
import com.fieldflow.feature.business.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessDetailViewModel @Inject constructor(
    private val repository: BusinessRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val businessId: String = checkNotNull(savedStateHandle["businessId"])
    
    private val _uiState = MutableStateFlow<BusinessDetailUiState>(BusinessDetailUiState.Loading)
    val uiState: StateFlow<BusinessDetailUiState> = _uiState.asStateFlow()
    
    private val _events = Channel<BusinessDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    init {
        loadBusinessDetail()
    }
    
    private fun loadBusinessDetail() {
        viewModelScope.launch {
            repository.getBusinessDetail(businessId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = BusinessDetailUiState.Success(result.data)
                    }
                    is Result.Error -> {
                        val currentState = _uiState.value
                        if (currentState is BusinessDetailUiState.Success) {
                            _events.send(BusinessDetailEvent.ShowSnackbar(result.message))
                        } else {
                            _uiState.value = BusinessDetailUiState.Error(result.message)
                        }
                    }
                }
            }
        }
    }
    
    fun refresh() {
        loadBusinessDetail()
    }
    
    fun toggleFavorite() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is BusinessDetailUiState.Success) {
                val updatedBusiness = currentState.business.copy(isFavorite = !currentState.business.isFavorite)
                _uiState.value = currentState.copy(business = updatedBusiness)
                
                when (val result = repository.toggleFavorite(businessId)) {
                    is Result.Success -> {
                        val message = if (updatedBusiness.isFavorite) "Added to favorites" else "Removed from favorites"
                        _events.send(BusinessDetailEvent.ShowSnackbar(message))
                    }
                    is Result.Error -> {
                        _uiState.value = currentState
                        _events.send(BusinessDetailEvent.ShowSnackbar("Failed to update favorite"))
                    }
                }
            }
        }
    }
    
    fun toggleHidden() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is BusinessDetailUiState.Success) {
                when (val result = repository.toggleHidden(businessId)) {
                    is Result.Success -> {
                        _events.send(BusinessDetailEvent.ShowSnackbar("Business hidden"))
                        _events.send(BusinessDetailEvent.NavigateBack)
                    }
                    is Result.Error -> {
                        _events.send(BusinessDetailEvent.ShowSnackbar("Failed to hide business"))
                    }
                }
            }
        }
    }
    
    fun onPhoneClick(phoneNumber: String) {
        viewModelScope.launch {
            _events.send(BusinessDetailEvent.NavigateToDialer(phoneNumber))
        }
    }
    
    fun onEmailClick(email: String) {
        viewModelScope.launch {
            _events.send(BusinessDetailEvent.NavigateToEmail(email))
        }
    }
    
    fun onWebsiteClick(url: String) {
        viewModelScope.launch {
            _events.send(BusinessDetailEvent.NavigateToWebsite(url))
        }
    }
    
    fun onDirectionsClick(lat: Double, long: Double) {
        viewModelScope.launch {
            _events.send(BusinessDetailEvent.NavigateToMaps(lat, long))
        }
    }
    
    fun onBackClick() {
        viewModelScope.launch {
            _events.send(BusinessDetailEvent.NavigateBack)
        }
    }
}