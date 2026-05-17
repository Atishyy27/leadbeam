package com.fieldflow.feature.map.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fieldflow.feature.map.domain.model.MapBusinessItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val businesses by viewModel.visibleBusinesses.collectAsState()
    val scope = rememberCoroutineScope()
    var selectedBusiness by remember { mutableStateOf<MapBusinessItem?>(null) }
    var isLocationGranted by remember { mutableStateOf(false) }

    com.fieldflow.feature.map.ui.components.LocationPermissionHandler(
        onPermissionResult = { granted ->
            isLocationGranted = granted
        }
    )

    val defaultLocation = LatLng(30.2672, -97.7431)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            cameraPositionState.projection?.visibleRegion?.latLngBounds?.let { bounds ->
                viewModel.onCameraMoved(bounds)
            }
        }
    }

    // WRAP EVERYTHING IN A BOX SO THE SEARCH BAR FLOATS ON TOP
    Box(modifier = modifier.fillMaxSize()) {
        
        // 1. THE MAP (Base Layer)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = isLocationGranted 
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true
            )
        ) {
            Clustering(
                items = businesses,
                onClusterClick = { cluster ->
                    scope.launch {
                        val zoomLevel = cameraPositionState.position.zoom + 2f
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(cluster.position, zoomLevel),
                            durationMs = 500
                        )
                    }
                    true
                },
                onClusterItemClick = { business ->
                    selectedBusiness = business
                    false
                }
            )
        }

        // 2. THE SEARCH & FILTER BAR (Floating Top Layer)
        val searchQuery by viewModel.searchQuery.collectAsState()
        val selectedCategory by viewModel.selectedCategory.collectAsState()
        val categories = listOf("Restaurant", "Retail", "Service", "Healthcare")

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search businesses...") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { 
                            if (selectedCategory == category) viewModel.updateCategory(null) 
                            else viewModel.updateCategory(category) 
                        },
                        label = { Text(category) }
                    )
                }
            }
        }
    }

    // 3. THE BOTTOM SHEET (Renders when a pin is clicked)
    selectedBusiness?.let { business ->
        ModalBottomSheet(
            onDismissRequest = { selectedBusiness = null },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth() // Don't use fillMaxSize in a bottom sheet!
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
                        // ACTUALLY SAVES TO THE DATABASE NOW
                        viewModel.addBusinessToRoute(business)
                        selectedBusiness = null 
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Add to Today's Route")
                }
            }
        }
    }
}