package com.fieldflow.feature.route.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.core.database.dao.RouteDao
import com.fieldflow.core.database.entity.RouteStopEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val routeDao: RouteDao
) : ViewModel() {

    private val _routeStops = MutableStateFlow<List<RouteStopEntity>>(emptyList())
    val routeStops: StateFlow<List<RouteStopEntity>> = _routeStops.asStateFlow()

    init {
        loadActiveRoute()
    }

    private fun loadActiveRoute() {
        viewModelScope.launch {
            // Use the synchronous database entity accessor function
            val activeRoute = routeDao.getActiveRouteEntity() 
            if (activeRoute != null) {
                routeDao.getStopsForRoute(activeRoute.id).collect { stops ->
                    _routeStops.value = stops
                }
            }
        }
    }

    fun removeStop(stop: RouteStopEntity) {
        viewModelScope.launch {
            routeDao.deleteRouteStop(stop)
            loadActiveRoute() // Refresh the list
        }
    }
}