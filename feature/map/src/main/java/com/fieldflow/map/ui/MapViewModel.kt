package com.fieldflow.feature.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.feature.map.domain.model.MapBusinessItem
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getBusinessesInBoundsUseCase: com.fieldflow.feature.map.domain.usecase.GetBusinessesInBoundsUseCase
    private val routeDao: com.fieldflow.core.database.dao.RouteDao
) : ViewModel() {
    
    private var fetchJob: kotlinx.coroutines.Job? = null
    private val _visibleBusinesses = MutableStateFlow<List<MapBusinessItem>>(emptyList())
    val visibleBusinesses: StateFlow<List<MapBusinessItem>> = _visibleBusinesses.asStateFlow()

    private val cameraBoundsFlow = MutableStateFlow<LatLngBounds?>(null)

    init {
        setupDebouncedBoundsObserver()
    }

    private fun setupDebouncedBoundsObserver() {
        cameraBoundsFlow
            .debounce(300L) // 📍 300ms debounce prevents API hammering during map drag
            .distinctUntilChanged()
            .onEach { bounds ->
                if (bounds != null) {
                    fetchBusinessesInBounds(bounds)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onCameraMoved(bounds: LatLngBounds) {
        cameraBoundsFlow.value = bounds
    }

    fun addBusinessToRoute(business: MapBusinessItem) {
        viewModelScope.launch {
            try {
                // 1. Get or Create an Active Route
                var activeRoute = routeDao.getActiveRoute()
                if (activeRoute == null) {
                    val routeId = "route_${System.currentTimeMillis()}"
                    activeRoute = com.fieldflow.core.database.entity.RouteEntity(
                        id = routeId,
                        date = System.currentTimeMillis(),
                        status = "active"
                    )
                    routeDao.insertRoute(activeRoute)
                }

                // 2. Figure out what stop number this is (Order Index)
                val stopCount = routeDao.getStopCountForRoute(activeRoute.id)

                // 3. Save the Business as a Stop
                val stop = com.fieldflow.core.database.entity.RouteStopEntity(
                    routeId = activeRoute.id,
                    businessId = business.leadbeamId,
                    businessName = business.businessName,
                    lat = business.lat,
                    long = business.long,
                    orderIndex = stopCount // Appends to the end of the route
                )
                routeDao.insertRouteStop(stop)

                println("MATRIX MODE: Successfully saved ${business.businessName} to DB as Stop #${stopCount + 1}")
                // (Optional: send a UI Event to show a "Added to Route!" Snackbar)
                
            } catch (e: Exception) {
                println("MATRIX MODE ERROR saving to route: ${e.message}")
            }
        }
    }
    
    private fun fetchBusinessesInBounds(bounds: LatLngBounds) {
        fetchJob?.cancel() // Cancel previous fetch if user pans again
        fetchJob = getBusinessesInBoundsUseCase(bounds)
            .onEach { businesses ->
                _visibleBusinesses.value = businesses
            }
            .launchIn(viewModelScope)
    }
}