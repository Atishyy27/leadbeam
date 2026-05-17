// feature/route/src/main/java/com/fieldflow/feature/route/ui/create/CreateRouteViewModel.kt
package com.fieldflow.feature.route.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.feature.business.domain.model.BusinessDetail
import com.fieldflow.feature.route.data.repository.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class CreateRouteUiState(
    val routeName: String = "",
    val routeDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val selectedBusinesses: List<BusinessDetail> = emptyList()
)

sealed class CreateRouteEvent {
    data class ShowError(val message: String) : CreateRouteEvent()
    object NavigateBack : CreateRouteEvent()
}

@HiltViewModel
class CreateRouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CreateRouteUiState())
    val uiState: StateFlow<CreateRouteUiState> = _uiState.asStateFlow()
    
    private val _events = Channel<CreateRouteEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    fun updateRouteName(name: String) {
        _uiState.value = _uiState.value.copy(routeName = name)
    }
    
    fun updateRouteDate(date: String) {
        _uiState.value = _uiState.value.copy(routeDate = date)
    }
    
    fun addBusiness(business: BusinessDetail) {
        val current = _uiState.value.selectedBusinesses
        if (!current.any { it.leadbeamId == business.leadbeamId }) {
            _uiState.value = _uiState.value.copy(selectedBusinesses = current + business)
        }
    }
    
    fun removeBusiness(businessId: String) {
        _uiState.value = _uiState.value.copy(
            selectedBusinesses = _uiState.value.selectedBusinesses.filter { it.leadbeamId != businessId }
        )
    }
    
    fun moveBusinessUp(businessId: String) {
        val businesses = _uiState.value.selectedBusinesses.toMutableList()
        val index = businesses.indexOfFirst { it.leadbeamId == businessId }
        if (index > 0) {
            val temp = businesses[index]
            businesses[index] = businesses[index - 1]
            businesses[index - 1] = temp
            _uiState.value = _uiState.value.copy(selectedBusinesses = businesses)
        }
    }
    
    fun moveBusinessDown(businessId: String) {
        val businesses = _uiState.value.selectedBusinesses.toMutableList()
        val index = businesses.indexOfFirst { it.leadbeamId == businessId }
        if (index < businesses.size - 1) {
            val temp = businesses[index]
            businesses[index] = businesses[index + 1]
            businesses[index + 1] = temp
            _uiState.value = _uiState.value.copy(selectedBusinesses = businesses)
        }
    }
    
    fun saveRoute() {
        viewModelScope.launch {
            val state = _uiState.value
            
            if (state.routeName.isBlank()) {
                _events.send(CreateRouteEvent.ShowError("Route name is required"))
                return@launch
            }
            
            if (state.selectedBusinesses.size < 2) {
                _events.send(CreateRouteEvent.ShowError("Add at least 2 businesses"))
                return@launch
            }
            
            try {
                routeRepository.createRoute(
                    name = state.routeName,
                    date = state.routeDate,
                    businessIds = state.selectedBusinesses.map { it.leadbeamId }
                )
                _events.send(CreateRouteEvent.NavigateBack)
            } catch (e: Exception) {
                _events.send(CreateRouteEvent.ShowError("Failed to save route"))
            }
        }
    }
}