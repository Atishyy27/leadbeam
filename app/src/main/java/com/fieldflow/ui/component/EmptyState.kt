// app/src/main/java/com/fieldflow/ui/components/EmptyState.kt
package com.fieldflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        
        Spacer(Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(Modifier.height(24.dp))
            Button(onClick = onActionClick) {
                Text(actionText)
            }
        }
    }
}

// Specific empty states
@Composable
fun NoBusinessesFound() {
    EmptyState(
        icon = Icons.Filled.SearchOff,
        title = "No businesses found",
        message = "Try adjusting your filters or zoom out on the map"
    )
}

@Composable
fun NoRoutesYet(onCreateRoute: () -> Unit) {
    EmptyState(
        icon = Icons.Filled.Route,
        title = "No routes yet",
        message = "Create your first route to start planning your visits",
        actionText = "Create Route",
        onActionClick = onCreateRoute
    )
}

@Composable
fun NetworkError(onRetry: () -> Unit) {
    EmptyState(
        icon = Icons.Filled.CloudOff,
        title = "Connection error",
        message = "Unable to fetch data. Check your connection and try again.",
        actionText = "Retry",
        onActionClick = onRetry
    )
}