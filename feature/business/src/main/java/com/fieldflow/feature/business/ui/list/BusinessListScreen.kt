// feature/business/src/main/java/com/fieldflow/feature/business/ui/list/BusinessListScreen.kt
package com.fieldflow.feature.business.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldflow.feature.business.ui.components.BusinessCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessListScreen(
    onNavigateBack: () -> Unit,
    onBusinessClick: (String) -> Unit,
    viewModel: BusinessListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var showSortMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Businesses") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, "Sort")
                    }
                    
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Name") },
                            onClick = {
                                viewModel.setSortBy("name")
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Rating") },
                            onClick = {
                                viewModel.setSortBy("rating")
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Confidence") },
                            onClick = {
                                viewModel.setSortBy("confidence")
                                showSortMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is BusinessListUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is BusinessListUiState.Success -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.businesses, key = { it.leadbeamId }) { business ->
                        BusinessCard(
                            business = business,
                            onClick = { onBusinessClick(business.leadbeamId) }
                        )
                    }
                    
                    if (state.hasMore) {
                        item {
                            LaunchedEffect(Unit) {
                                viewModel.loadMore()
                            }
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
            is BusinessListUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}