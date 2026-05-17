// feature/route/src/main/java/com/fieldflow/feature/route/ui/detail/RouteDetailScreen.kt
package com.fieldflow.feature.route.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldflow.feature.route.ui.components.OptimizationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: RouteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RouteDetailEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is RouteDetailEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    when (val state = uiState) {
                        is RouteDetailUiState.Success -> Text(state.route.name)
                        else -> Text("Route Details")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is RouteDetailUiState.Success -> {
                            // NEW: Start Route Button
                            if (!state.route.isCompleted && state.route.stops.isNotEmpty()) {
                                IconButton(onClick = { viewModel.startExecution() }) {
                                    Icon(Icons.Filled.PlayArrow, "Start route")
                                }
                            }
                            
                            // EXISTING: Optimize Button
                            if (!state.route.isCompleted) {
                                IconButton(
                                    onClick = { viewModel.optimizeRoute() },
                                    enabled = !state.isOptimizing
                                ) {
                                    if (state.isOptimizing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(Icons.Filled.AutoAwesome, "Optimize")
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (val state = uiState) {
            is RouteDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is RouteDetailUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Route info card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = "${state.route.stopCount} stops",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Date: ${state.route.date}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            state.route.totalDistance?.let { distance ->
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Distance: ${String.format("%.1f", distance)} miles",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            state.route.totalDuration?.let { duration ->
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Duration: ${duration} minutes",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    
                    // Stops list
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(state.route.stops, key = { _, stop -> stop.id }) { index, stop ->
                            RouteStopCard(
                                stop = stop,
                                index = index
                            )
                        }
                    }
                }
                
                // Optimization dialog
                if (state.optimizationResult != null) {
                    OptimizationDialog(
                        result = state.optimizationResult,
                        onAccept = {
                            viewModel.acceptOptimization()
                        },
                        onDismiss = {
                            viewModel.dismissOptimization()
                        }
                    )
                }
            }
            is RouteDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun RouteStopCard(
    stop: com.fieldflow.feature.route.data.model.RouteStop,
    index: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = if (stop.isVisited) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "${index + 1}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (stop.isVisited) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stop.business.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stop.business.addressFull,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (stop.isVisited) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "✓ Visited",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}