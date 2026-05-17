// feature/business/src/main/java/com/fieldflow/feature/business/ui/components/BusinessDetailHeader.kt
package com.fieldflow.feature.business.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fieldflow.feature.business.domain.model.BusinessDetail

@Composable
fun BusinessDetailHeader(
    business: BusinessDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = business.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(Modifier.height(8.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = {},
                label = { Text(business.categoryDisplay) }
            )
            
            if (business.isChain && business.chainName != null) {
                AssistChip(
                    onClick = {},
                    label = { Text("Chain") }
                )
            }
        }
        
        Spacer(Modifier.height(12.dp))
        
        business.rating?.let { rating ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (index < rating.toInt()) Color(0xFFFFB800) else Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = String.format("%.1f", rating),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                business.reviewsCount?.let { count ->
                    Text(
                        text = "($count reviews)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        business.isOpenNow?.let { isOpen ->
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (isOpen) Color(0xFF4CAF50) else Color(0xFFF44336),
                            shape = MaterialTheme.shapes.small
                        )
                )
                Text(
                    text = if (isOpen) "Open now" else "Closed",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isOpen) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }
    }
}