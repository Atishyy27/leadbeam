// feature/route/src/main/java/com/fieldflow/feature/route/ui/components/RouteProgressSheet.kt
package com.fieldflow.feature.route.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fieldflow.feature.route.data.model.RouteExecutionState
import com.fieldflow.feature.route.data.model.RouteStop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteProgressSheet(
    executionState: RouteExecutionState,
    onNavigate: (RouteStop) -> Unit,
    onMarkVisited: (RouteStop) -> Unit,
    onUndo: () -> Unit,
    onComplete: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = executionState.progressText,
                        style = MaterialTheme.typography.titleLarge
                    )
                    LinearProgressIndicator(
                        progress = { executionState.progress },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(top = 4.dp)
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (executionState.lastVisitedStopId != null) {
                        IconButton(onClick = onUndo) {
                            Icon(Icons.Filled.Undo, "Undo last visit")
                        }
                    }
                    
                    if (executionState.isCompleted) {
                        Button(onClick = onComplete) {
                            Text("Complete")
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Stops list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(executionState.route.stops, key = { _, stop -> stop.id }) { index, stop ->
                    RouteStopExecutionCard(
                        stop = stop,
                        index = index,
                        isVisited = executionState.visitedStopIds.contains(stop.id),
                        isCurrent = index == executionState.currentStopIndex,
                        onNavigate = { onNavigate(stop) },
                        onMarkVisited = { onMarkVisited(stop) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteStopExecutionCard(
    stop: RouteStop,
    index: Int,
    isVisited: Boolean,
    isCurrent: Boolean,
    onNavigate: () -> Unit,
    onMarkVisited: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isVisited -> MaterialTheme.colorScheme.primaryContainer
                isCurrent -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = if (isVisited) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "${index + 1}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isVisited) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    }
                    
                    Column {
                        Text(
                            text = stop.business.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stop.business.addressFull,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (isVisited) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Visited",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            if (!isVisited) {
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigate,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Navigation, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Navigate")
                    }
                    
                    if (isCurrent) {
                        FilledTonalButton(
                            onClick = onMarkVisited,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Check, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Mark Visited")
                        }
                    }
                }
            }
        }
    }
}