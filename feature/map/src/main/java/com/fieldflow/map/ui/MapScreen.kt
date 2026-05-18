package com.fieldflow.feature.map.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.fieldflow.feature.map.domain.model.MapBusinessItem
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit = {}, // Added for your Back Arrow snippet
    onNavigateToList: () -> Unit = {},
    onBusinessClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val businesses by viewModel.visibleBusinesses.collectAsState()
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
            // Replaced Clustering with standard Markers to support a11y contentDescriptions
            businesses.forEach { business ->
                Marker(
                    state = MarkerState(position = business.position), // assuming MapBusinessItem has position from ClusterItem
                    title = business.businessName,
                    snippet = business.category,
                    contentDescription = "${business.businessName}, ${business.category}", // A11y description
                    onClick = {
                        selectedBusiness = business
                        onBusinessClick(business.id)
                        false
                    }
                )
            }
        }

        // 2. STATUS INDICATORS (Loading & Offline)
        val isLoading by viewModel.isLoading.collectAsState()
        val isOffline by viewModel.isOffline.collectAsState()

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (isOffline) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 120.dp, end = 16.dp), 
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "Offline Mode (Cached)",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // 3. THE SEARCH & FILTER BAR (Floating Top Layer)
        val searchQuery by viewModel.searchQuery.collectAsState()
        val selectedCategory by viewModel.selectedCategory.collectAsState()
        val categories = listOf("Restaurant", "Retail", "Service", "Healthcare")

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Applied your Nav Icon snippet here
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.semantics { contentDescription = "Go back" }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) // description is in semantics
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search businesses...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    }
                )

                // List Navigation Button
                IconButton(
                    onClick = onNavigateToList,
                    modifier = Modifier.semantics { contentDescription = "View businesses as list" },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Icon(Icons.Filled.List, contentDescription = null)
                }
            }

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

    // 4. THE BOTTOM SHEET
    selectedBusiness?.let { business ->
        ModalBottomSheet(
            onDismissRequest = { selectedBusiness = null },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                
                // Applied your Action Button semantics here
                Button(
                    onClick = { 
                        viewModel.addBusinessToRoute(business)
                        selectedBusiness = null 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .semantics { contentDescription = "Add ${business.businessName} to today's route" }
                ) {
                    Text("Add to Today's Route")
                }
            }
        }
    }
}