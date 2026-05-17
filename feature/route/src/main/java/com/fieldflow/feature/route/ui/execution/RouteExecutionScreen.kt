// feature/route/src/main/java/com/fieldflow/feature/route/ui/execution/RouteExecutionScreen.kt
package com.fieldflow.feature.route.ui.execution

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldflow.feature.route.ui.components.RouteProgressSheet
import com.fieldflow.util.RequestNotificationPermission // NEW IMPORT
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteExecutionScreen(
    onNavigateBack: () -> Unit,
    viewModel: RouteExecutionViewModel = hiltViewModel()
) {
    // Automatically request POST_NOTIFICATIONS permission on Android 13+
    RequestNotificationPermission()
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RouteExecutionEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is RouteExecutionEvent.NavigateToMaps -> {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=${event.lat},${event.long}")
                    )
                    context.startActivity(intent)
                }
                RouteExecutionEvent.RouteCompleted -> {
                    snackbarHostState.showSnackbar("Route completed!")
                    onNavigateBack()
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    when (val state = uiState) {
                        is RouteExecutionUiState.Active -> Text(state.executionState.route.name)
                        else -> Text("Route Execution")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            when (val state = uiState) {
                is RouteExecutionUiState.Active -> {
                    if (showBottomSheet) {
                        RouteProgressSheet(
                            executionState = state.executionState,
                            onNavigate = { stop ->
                                viewModel.navigateToStop(stop.business.lat, stop.business.long)
                            },
                            onMarkVisited = { stop ->
                                viewModel.markStopVisited(stop.id)
                            },
                            onUndo = {
                                viewModel.undoLastVisit()
                            },
                            onComplete = {
                                viewModel.completeRoute()
                            },
                            onDismiss = { showBottomSheet = false }
                        )
                    }
                }
                else -> {}
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is RouteExecutionUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is RouteExecutionUiState.NoActiveRoute -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No active route")
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Go to Routes")
                        }
                    }
                }
                is RouteExecutionUiState.Active -> {
                    RouteMapView(
                        executionState = state.executionState,
                        onMarkerClick = { stop ->
                            viewModel.markStopVisited(stop.id)
                        }
                    )
                    
                    // Show/hide bottom sheet FAB
                    if (!showBottomSheet) {
                        FloatingActionButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Icon(Icons.Filled.List, "Show stops")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteMapView(
    executionState: com.fieldflow.feature.route.data.model.RouteExecutionState,
    onMarkerClick: (com.fieldflow.feature.route.data.model.RouteStop) -> Unit
) {
    val stops = executionState.route.stops
    val center = if (stops.isNotEmpty()) {
        LatLng(stops[0].business.lat, stops[0].business.long)
    } else {
        LatLng(0.0, 0.0)
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(center, 12f)
    }
    
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        stops.forEachIndexed { index, stop ->
            val position = LatLng(stop.business.lat, stop.business.long)
            val isCurrent = index == executionState.currentStopIndex
            val isVisited = executionState.visitedStopIds.contains(stop.id)
            
            Marker(
                state = MarkerState(position = position),
                title = "${index + 1}. ${stop.business.name}",
                snippet = if (isVisited) "✓ Visited" else if (isCurrent) "Current stop" else "Upcoming",
                onClick = {
                    if (!isVisited) {
                        onMarkerClick(stop)
                    }
                    true
                }
            )
        }
    }
}