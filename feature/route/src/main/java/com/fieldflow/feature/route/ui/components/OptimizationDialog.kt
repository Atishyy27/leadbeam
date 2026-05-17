// feature/route/src/main/java/com/fieldflow/feature/route/ui/components/OptimizationDialog.kt
package com.fieldflow.feature.route.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fieldflow.feature.route.data.model.OptimizationResult

@Composable
fun OptimizationDialog(
    result: OptimizationResult,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Text(
                    text = "Route Optimization",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(16.dp))
                
                // Savings summary
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Potential Savings",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SavingsItem(
                                icon = Icons.Filled.Route,
                                label = "Distance",
                                value = "${String.format("%.1f", result.distanceSaved)} mi",
                                percent = result.distanceSavedPercent
                            )
                            
                            SavingsItem(
                                icon = Icons.Filled.AccessTime,
                                label = "Time",
                                value = "${result.timeSaved} min",
                                percent = result.timeSavedPercent
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Before/After comparison
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    ComparisonSection(
                        title = "Original Route",
                        distance = result.originalDistance,
                        duration = result.originalDuration,
                        stops = result.originalOrder.map { it.business.name }
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    ComparisonSection(
                        title = "Optimized Route",
                        distance = result.optimizedDistance,
                        duration = result.optimizedDuration,
                        stops = result.optimizedOrder.map { it.business.name },
                        isOptimized = true
                    )
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Keep Original")
                    }
                    
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Accept")
                    }
                }
            }
        }
    }
}

@Composable
private fun SavingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    percent: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Save $percent%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ComparisonSection(
    title: String,
    distance: Double,
    duration: Int,
    stops: List<String>,
    isOptimized: Boolean = false
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isOptimized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "${String.format("%.1f", distance)} mi",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$duration min",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(Modifier.height(8.dp))
        
        stops.forEachIndexed { index, stopName ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = if (isOptimized) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${index + 1}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    text = stopName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
            if (index < stops.size - 1) {
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}