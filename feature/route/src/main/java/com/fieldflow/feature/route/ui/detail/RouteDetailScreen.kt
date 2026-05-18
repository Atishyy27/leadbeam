// feature/route/src/main/java/com/fieldflow/feature/route/ui/detail/RouteDetailScreen.kt
package com.fieldflow.feature.route.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    onNavigateBack: () -> Unit,
    onStartExecution: (String) -> Unit,
    viewModel: RouteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is RouteDetailUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RouteDetailUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Route header
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = state.route.name,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "${state.route.stops.size} stops • ${state.route.date}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // PROMINENT START BUTTON
                    Button(
                        onClick = { onStartExecution(state.route.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Start Route", style = MaterialTheme.typography.titleMedium)
                    }

                    // Stops list
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(state.route.stops) { index, stop ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (stop.isVisited)
                                        MaterialTheme.colorScheme.surfaceVariant
                                    else
                                        MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        // FIX: stop.business.name (not stop.businessName)
                                        Text(
                                            text = "${index + 1}. ${stop.business.name}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        
                                        // Address with null safety
                                        val addressParts = listOfNotNull(
                                            stop.business.addressFull.takeIf { it.isNotBlank() },
                                            listOfNotNull(
                                                stop.business.city.takeIf { it.isNotBlank() },
                                                stop.business.state.takeIf { it.isNotBlank() }
                                            ).joinToString(", ").takeIf { it.isNotBlank() }
                                        )
                                        
                                        if (addressParts.isNotEmpty()) {
                                            Text(
                                                text = addressParts.joinToString("\n"),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

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
                    }
                }
            }
            is RouteDetailUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { /* Retry */ }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}