// feature/business/src/main/java/com/fieldflow/feature/business/ui/components/ConfidenceIndicator.kt
package com.fieldflow.feature.business.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConfidenceIndicator(
    confidence: Double,
    completeness: Double,
    sources: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Data Quality", style = MaterialTheme.typography.titleMedium)
        
        Spacer(Modifier.height(12.dp))
        
        if (confidence < 0.7) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.error)
                    Text(
                        "Low confidence data. Verify before visiting.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricColumn("Confidence", confidence, Modifier.weight(1f))
            MetricColumn("Completeness", completeness, Modifier.weight(1f))
        }
        
        Spacer(Modifier.height(12.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Verified, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Text(
                "Verified from $sources ${if (sources == 1) "source" else "sources"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MetricColumn(label: String, value: Double, modifier: Modifier = Modifier) {
    val percentage = (value * 100).toInt()
    val color = when {
        value >= 0.9 -> Color(0xFF4CAF50)
        value >= 0.7 -> Color(0xFFFFB800)
        else -> Color(0xFFF44336)
    }
    
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text("$percentage%", style = MaterialTheme.typography.titleLarge, color = color)
    }
}