// feature/map/src/main/java/com/fieldflow/feature/map/ui/MapViewModel.kt
package com.fieldflow.feature.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.feature.map.domain.model.MapBusinessItem
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted       // FIX: was missing, caused "Unresolved reference"
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine               // FIX: was missing, caused "Unresolved reference"
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getBusinessesInBoundsUseCase: com.fieldflow.feature.map.domain.usecase.GetBusinessesInBoundsUseCase,
    private val routeDao: com.fieldflow.core.database.dao.RouteDao
) : ViewModel() {

    private var fetchJob: Job? = null

    private val _rawBusinesses = MutableStateFlow<List<MapBusinessItem>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    val visibleBusinesses: StateFlow<List<MapBusinessItem>> = combine(
        _rawBusinesses,
        _searchQuery,
        _selectedCategory
    ) { rawBusinesses, query, category ->
        var filtered = rawBusinesses

        // FIX: Case-insensitive + partial match
        // Handles API mismatches like "Restaurant" vs "restaurants" vs "food_and_drink"
        if (category != null && category.isNotBlank()) {
            filtered = filtered.filter { business ->
                business.category.equals(category, ignoreCase = true) ||
                business.category.contains(category, ignoreCase = true)
            }
        }

        if (query.isNotBlank()) {
            filtered = filtered.filter { business ->
                business.businessName.contains(query, ignoreCase = true) ||
                business.category.contains(query, ignoreCase = true)
            }
        }

        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val categoryGroupMapping = mapOf(
        "Restaurant" to "food_and_drink", // Note: match whatever the mock API actually sends
        "Retail" to "retail_and_shopping", 
        "Service" to "home_services",
        "Healthcare" to "health_and_medical"
    )

    private fun fetchBusinessesInBounds(bounds: LatLngBounds) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                val categoryGroup = _selectedCategory.value?.let { categoryGroupMapping[it] }
                getBusinessesInBoundsUseCase(bounds, categoryGroup).collect { businesses ->
                    println("FIELDFLOW_DEBUG: Fetched ${businesses.size} businesses")
                    if (businesses.isNotEmpty()) {
                        val uniqueCategories = businesses.map { it.category }.distinct()
                        println("FIELDFLOW_DEBUG: Unique categories = $uniqueCategories")
                    }
                    _rawBusinesses.value = businesses
                    _isOffline.value = false
                }
            } catch (e: Exception) {
                println("FIELDFLOW_DEBUG: Fetch failed: ${e.message}")
                _isOffline.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // FIX: Tapping the selected chip again clears the filter
    fun updateCategory(category: String?) {
        _selectedCategory.value = if (category == _selectedCategory.value) null else category
    }

    private val cameraBoundsFlow = MutableStateFlow<LatLngBounds?>(null)

    init {
        setupDebouncedBoundsObserver()
    }

    private fun setupDebouncedBoundsObserver() {
        cameraBoundsFlow
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { bounds -> if (bounds != null) fetchBusinessesInBounds(bounds) }
            .launchIn(viewModelScope)
    }

    fun onCameraMoved(bounds: LatLngBounds) {
        cameraBoundsFlow.value = bounds
    }

    fun addBusinessToRoute(business: MapBusinessItem) {
        viewModelScope.launch {
            try {
                val activeRoute = routeDao.getActiveRouteEntity()
                val activeRouteId = if (activeRoute == null) {
                    val routeId = "route_${System.currentTimeMillis()}"
                    val newRoute = com.fieldflow.core.database.entity.RouteEntity(
                        id = routeId,
                        name = "Today's Route",
                        date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                            .format(java.util.Date()),
                        status = "active",
                        isActive = true,
                        createdAt = System.currentTimeMillis()
                    )
                    routeDao.insertRoute(newRoute)
                    routeId
                } else {
                    activeRoute.id
                }

                val stopCount = routeDao.getStopCountForRoute(activeRouteId)

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
                println("FIELDFLOW_DEBUG: Added '${business.businessName}' to route $activeRouteId")
            } catch (e: Exception) {
                println("FIELDFLOW_DEBUG: Error adding to route: ${e.message}")
            }
        }
    }
}