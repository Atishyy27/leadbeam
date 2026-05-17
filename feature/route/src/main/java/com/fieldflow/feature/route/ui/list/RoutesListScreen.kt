// feature/route/src/main/java/com/fieldflow/feature/route/ui/list/RoutesListScreen.kt
package com.fieldflow.feature.route.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldflow.feature.route.ui.components.RouteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onRouteClick: (String) -> Unit,
    viewModel: RoutesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var routeToDelete by remember { mutableStateOf<String?>(null) }
    
    if (routeToDelete != null) {
        AlertDialog(
            onDismissRequest = { routeToDelete = null },
            title = { Text("Delete Route") },
            text = { Text("Are you sure you want to delete this route?") },
            confirmButton = {
                TextButton(onClick = {
                    routeToDelete?.let { viewModel.deleteRoute(it) }
                    routeToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { routeToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Routes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Filled.Add, "Create route")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (val state = uiState) {
            is RoutesListUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is RoutesListUiState.Success -> {
                if (state.groupedRoutes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "No routes yet",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Tap + to create your first route",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        state.groupedRoutes.forEach { (group, routes) ->
                            item {
                                Text(
                                    text = group,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            items(routes, key = { it.id }) { route ->
                                RouteCard(
                                    route = route,
                                    onClick = { onRouteClick(route.id) },
                                    onDelete = { routeToDelete = route.id }
                                )
                            }
                        }
                    }
                }
            }
            is RoutesListUiState.Error -> {
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