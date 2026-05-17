package com.fieldflow.feature.map.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fieldflow.feature.map.domain.model.MapBusinessItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val businesses by viewModel.visibleBusinesses.collectAsState()
    val scope = rememberCoroutineScope()
    
    // Track which business is currently selected for the Bottom Sheet
    var selectedBusiness by remember { mutableStateOf<MapBusinessItem?>(null) }

    // Default target: Austin, TX (Based on the Mock API data spec)
    val defaultLocation = LatLng(30.2672, -97.7431)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    // Observe camera movement state. When movement stops, grab the visible bounding box.
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            cameraPositionState.projection?.visibleRegion?.latLngBounds?.let { bounds ->
                viewModel.onCameraMoved(bounds)
            }
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = false // Will enable after handling runtime location permissions
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = true
        )
    ) {
        // High-performance rendering engine for large datasets
        Clustering(
            items = businesses,
            onClusterClick = { cluster ->
                // Zoom in smoothly when a cluster is tapped
                scope.launch {
                    val zoomLevel = cameraPositionState.position.zoom + 2f
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(cluster.position, zoomLevel),
                        durationMs = 500
                    )
                }
                true // Return true to indicate we handled the click completely
            },
            onClusterItemClick = { business ->
                // Show business details bottom sheet
                selectedBusiness = business
                false // Return false so Google Maps still auto-centers the selected pin
            }
        )
    }

    // Render Bottom Sheet if a business is selected
    selectedBusiness?.let { business ->
        ModalBottomSheet(
            onDismissRequest = { selectedBusiness = null },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = business.businessName,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = business.category,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Button(
                    onClick = { 
                        // TODO: Phase 5 Route Addition Logic
                        selectedBusiness = null 
                    },
                    modifier = Modifier.fillMaxSize() // Fills width nicely for a primary action
                ) {
                    Text("Add to Today's Route")
                }
            }
        }
    }
}