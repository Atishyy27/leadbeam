// feature/route/src/main/java/com/fieldflow/feature/route/ui/detail/RouteDetailViewModel.kt
package com.fieldflow.feature.route.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.feature.route.data.model.OptimizationResult
import com.fieldflow.feature.route.data.model.Route
import com.fieldflow.feature.route.data.repository.Result
import com.fieldflow.feature.route.data.repository.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RouteDetailUiState {
    object Loading : RouteDetailUiState()
    data class Success(
        val route: Route,
        val isOptimizing: Boolean = false,
        val optimizationResult: OptimizationResult? = null
    ) : RouteDetailUiState()
    data class Error(val message: String) : RouteDetailUiState()
}

sealed class RouteDetailEvent {
    data class ShowError(val message: String) : RouteDetailEvent()
    data class ShowSuccess(val message: String) : RouteDetailEvent()
}

@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val routeId: String = checkNotNull(savedStateHandle["routeId"])
    
    private val _uiState = MutableStateFlow<RouteDetailUiState>(RouteDetailUiState.Loading)
    val uiState: StateFlow<RouteDetailUiState> = _uiState.asStateFlow()
    
    private val _events = Channel<RouteDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    init {
        loadRoute()
    }
    
    private fun loadRoute() {
        viewModelScope.launch {
            routeRepository.getRouteById(routeId).collect { route ->
                if (route != null) {
                    val currentState = _uiState.value
                    val optimizationResult = if (currentState is RouteDetailUiState.Success) {
                        currentState.optimizationResult
                    } else null
                    
                    _uiState.value = RouteDetailUiState.Success(
                        route = route,
                        optimizationResult = optimizationResult
                    )
                } else {
                    _uiState.value = RouteDetailUiState.Error("Route not found")
                }
            }
        }
    }
    
    fun optimizeRoute() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState !is RouteDetailUiState.Success) return@launch
            
            // Show loading
            _uiState.value = currentState.copy(isOptimizing = true)
            
            // Call optimization
            when (val result = routeRepository.optimizeRoute(routeId)) {
                is Result.Success -> {
                    _uiState.value = currentState.copy(
                        isOptimizing = false,
                        optimizationResult = result.data
                    )
                }
                is Result.Error -> {
                    _uiState.value = currentState.copy(isOptimizing = false)
                    _events.send(RouteDetailEvent.ShowError(result.message))
                }
            }
        }
    }
    
    fun acceptOptimization() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState !is RouteDetailUiState.Success || currentState.optimizationResult == null) {
                return@launch
            }
            
            try {
                routeRepository.acceptOptimization(currentState.optimizationResult)
                _uiState.value = currentState.copy(optimizationResult = null)
                _events.send(RouteDetailEvent.ShowSuccess("Route optimized successfully"))
            } catch (e: Exception) {
                _events.send(RouteDetailEvent.ShowError("Failed to save optimization"))
            }
        }
    }
    
    fun dismissOptimization() {
        val currentState = _uiState.value
        if (currentState is RouteDetailUiState.Success) {
            _uiState.value = currentState.copy(optimizationResult = null)
        }
    }
}