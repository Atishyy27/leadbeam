// feature/route/src/main/java/com/fieldflow/feature/route/ui/create/CreateRouteScreen.kt
package com.fieldflow.feature.route.ui.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRouteScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBusinessPicker: () -> Unit,
    viewModel: CreateRouteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateRouteEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                CreateRouteEvent.NavigateBack -> onNavigateBack()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Route") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveRoute() },
                        enabled = uiState.selectedBusinesses.size >= 2
                    ) {
                        Text("SAVE")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToBusinessPicker) {
                Icon(Icons.Filled.Add, "Add business")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.routeName,
                onValueChange = { viewModel.updateRouteName(it) },
                label = { Text("Route Name") },
                placeholder = { Text("e.g., Tuesday Downtown") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = uiState.routeDate,
                onValueChange = { viewModel.updateRouteDate(it) },
                label = { Text("Date") },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Businesses (${uiState.selectedBusinesses.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                
                if (uiState.selectedBusinesses.size < 2) {
                    Text(
                        "Minimum 2 required",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            if (uiState.selectedBusinesses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Tap + to add businesses",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.selectedBusinesses, key = { it.leadbeamId }) { business ->
                        RouteStopItem(
                            business = business,
                            index = uiState.selectedBusinesses.indexOf(business),
                            onRemove = { viewModel.removeBusiness(business.leadbeamId) },
                            onMoveUp = { 
                                if (uiState.selectedBusinesses.indexOf(business) > 0) {
                                    viewModel.moveBusinessUp(business.leadbeamId)
                                }
                            },
                            onMoveDown = {
                                if (uiState.selectedBusinesses.indexOf(business) < uiState.selectedBusinesses.size - 1) {
                                    viewModel.moveBusinessDown(business.leadbeamId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteStopItem(
    business: com.fieldflow.feature.business.domain.model.BusinessDetail,
    index: Int,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${index + 1}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                
                Column {
                    Text(
                        text = business.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = business.addressFull,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row {
                IconButton(onClick = onMoveUp) {
                    Icon(Icons.Filled.KeyboardArrowUp, "Move up")
                }
                IconButton(onClick = onMoveDown) {
                    Icon(Icons.Filled.KeyboardArrowDown, "Move down")
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, "Remove")
                }
            }
        }
    }
}