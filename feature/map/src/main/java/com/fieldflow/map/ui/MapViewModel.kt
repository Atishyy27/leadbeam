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

    private fun fetchBusinessesInBounds(bounds: LatLngBounds) {
        fetchJob?.cancel() // Cancel previous fetch if user pans again
        fetchJob = getBusinessesInBoundsUseCase(bounds)
            .onEach { businesses ->
                _visibleBusinesses.value = businesses
            }
            .launchIn(viewModelScope)
    }
}