// feature/route/src/main/java/com/fieldflow/feature/route/ui/list/RoutesListViewModel.kt
package com.fieldflow.feature.route.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.feature.route.data.model.Route
import com.fieldflow.feature.route.data.repository.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

sealed class RoutesListUiState {
    object Loading : RoutesListUiState()
    data class Success(val groupedRoutes: Map<String, List<Route>>) : RoutesListUiState()
    data class Error(val message: String) : RoutesListUiState()
}

@HiltViewModel
class RoutesListViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<RoutesListUiState>(RoutesListUiState.Loading)
    val uiState: StateFlow<RoutesListUiState> = _uiState.asStateFlow()
    
    init {
        loadRoutes()
    }
    
    private fun loadRoutes() {
        viewModelScope.launch {
            try {
                routeRepository.getAllRoutes().collect { routes ->
                    val grouped = routes.groupBy { route ->
                        groupRoute(route.date)
                    }
                    _uiState.value = RoutesListUiState.Success(grouped)
                }
            } catch (e: Exception) {
                _uiState.value = RoutesListUiState.Error("Failed to load routes")
            }
        }
    }
    
    fun deleteRoute(routeId: String) {
        viewModelScope.launch {
            try {
                routeRepository.deleteRoute(routeId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    private fun groupRoute(dateString: String): String {
        return try {
            val routeDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            val today = LocalDate.now()
            val daysDiff = ChronoUnit.DAYS.between(routeDate, today).toInt()
            
            when {
                daysDiff == 0 -> "Today"
                daysDiff == -1 -> "Tomorrow"
                daysDiff == 1 -> "Yesterday"
                daysDiff in 2..6 -> "This Week"
                daysDiff in -6..-2 -> "Upcoming"
                daysDiff > 6 -> "Older"
                else -> "Future"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }
}