package com.fieldflow.feature.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.feature.map.domain.model.MapBusinessItem
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getBusinessesInBoundsUseCase: com.fieldflow.feature.map.domain.usecase.GetBusinessesInBoundsUseCase,
    private val routeDao: com.fieldflow.core.database.dao.RouteDao
) : ViewModel() {
    
    private var fetchJob: kotlinx.coroutines.Job? = null
    
    // 1. Add these variables to hold our raw data and filter states
    private val _rawBusinesses = MutableStateFlow<List<MapBusinessItem>>(emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    // 2. THIS IS THE MAGIC: It automatically filters whenever data, search, or category changes!
    val visibleBusinesses: StateFlow<List<MapBusinessItem>> = kotlinx.coroutines.flow.combine(
        _rawBusinesses,
        _searchQuery,
        _selectedCategory
    ) { businesses, query, category ->
        businesses.filter { business ->
            val matchesCategory = category == null || business.category == category
            val matchesSearch = query.isBlank() || business.businessName.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    // 3. Update your fetch function to save to _rawBusinesses instead of _visibleBusinesses
    private fun fetchBusinessesInBounds(bounds: LatLngBounds) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                getBusinessesInBoundsUseCase(bounds).collect { businesses ->
                    // Explicit diagnostic trace to check parsing success
                    println("FIELDFLOW_DEBUG: UseCase collected ${businesses.size} items")
                    if (businesses.isNotEmpty()) {
                        println("FIELDFLOW_DEBUG: Sample item name = ${businesses.first().businessName}")
                    }
                    
                    _rawBusinesses.value = businesses
                    _isOffline.value = false // Success means we are online
                }
            } catch (e: Exception) {
                println("FIELDFLOW_DEBUG: Exception inside collection block: ${e.message}")
                _isOffline.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 4. Add the UI Event Triggers
    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateCategory(category: String?) { _selectedCategory.value = category }

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
                // 1. Get Active Route Entity synchronously via suspend function
                val activeRoute = routeDao.getActiveRouteEntity()
                val activeRouteId = if (activeRoute == null) {
                    val routeId = "route_${System.currentTimeMillis()}"
                    val newRoute = com.fieldflow.core.database.entity.RouteEntity(
                        id = routeId,
                        name = "Today's Route",
                        date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()),
                        status = "active",
                        isActive = true,
                        createdAt = System.currentTimeMillis()
                    )
                    routeDao.insertRoute(newRoute)
                    routeId
                } else {
                    activeRoute.id
                }

                // 2. Fetch the stop count using the extracted String ID
                val stopCount = routeDao.getStopCountForRoute(activeRouteId)

                // 3. Construct RouteStopEntity to match your exact columns (String id, no lat/long/name fields)
                val stop = com.fieldflow.core.database.entity.RouteStopEntity(
                    id = "stop_${System.currentTimeMillis()}",
                    routeId = activeRouteId,
                    businessId = business.id,
                    businessName = business.businessName,
                    lat = business.location.latitude,
                    long = business.location.longitude,
                    orderIndex = stopCount,
                    isVisited = false,
                    visitedAt = null,
                    notes = null
                )
                routeDao.insertRouteStop(stop)

                println("MATRIX MODE: Successfully saved stop to DB")
            } catch (e: Exception) {
                println("MATRIX MODE ERROR saving to route: ${e.message}")
            }
        }
    }
}