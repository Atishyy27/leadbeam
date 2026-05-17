// feature/route/src/main/java/com/fieldflow/feature/route/ui/execution/RouteExecutionViewModel.kt
package com.fieldflow.feature.route.ui.execution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.feature.route.data.repository.RouteRepository
import com.fieldflow.feature.route.notification.RouteNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteExecutionViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val notificationManager: RouteNotificationManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<RouteExecutionUiState>(RouteExecutionUiState.Loading)
    val uiState: StateFlow<RouteExecutionUiState> = _uiState.asStateFlow()
    
    private val _events = Channel<RouteExecutionEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    init {
        loadActiveRoute()
    }
    
    private fun loadActiveRoute() {
        viewModelScope.launch {
            routeRepository.getActiveRoute().collect { executionState ->
                _uiState.value = if (executionState != null) {
                    // Update notification
                    notificationManager.updateRouteProgress(
                        routeName = executionState.route.name,
                        progress = executionState.progressText
                    )
                    RouteExecutionUiState.Active(executionState)
                } else {
                    notificationManager.cancelNotification()
                    RouteExecutionUiState.NoActiveRoute
                }
            }
        }
    }
    
    fun markStopVisited(stopId: String) {
        viewModelScope.launch {
            try {
                routeRepository.markStopVisited(stopId)
                _events.send(RouteExecutionEvent.ShowMessage("Stop marked as visited"))
            } catch (e: Exception) {
                _events.send(RouteExecutionEvent.ShowMessage("Failed to mark stop"))
            }
        }
    }
    
    fun undoLastVisit() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is RouteExecutionUiState.Active) {
                val lastVisitedId = currentState.executionState.lastVisitedStopId
                if (lastVisitedId != null) {
                    try {
                        routeRepository.undoLastVisit(lastVisitedId)
                        _events.send(RouteExecutionEvent.ShowMessage("Last visit undone"))
                    } catch (e: Exception) {
                        _events.send(RouteExecutionEvent.ShowMessage("Failed to undo"))
                    }
                }
            }
        }
    }
    
    fun navigateToStop(lat: Double, long: Double) {
        viewModelScope.launch {
            _events.send(RouteExecutionEvent.NavigateToMaps(lat, long))
        }
    }
    
    fun completeRoute() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is RouteExecutionUiState.Active) {
                try {
                    routeRepository.completeRoute(currentState.executionState.route.id)
                    notificationManager.cancelNotification()
                    _events.send(RouteExecutionEvent.RouteCompleted)
                } catch (e: Exception) {
                    _events.send(RouteExecutionEvent.ShowMessage("Failed to complete route"))
                }
            }
        }
    }
}