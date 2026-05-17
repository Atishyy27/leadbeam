// feature/business/src/main/java/com/fieldflow/feature/business/ui/components/ContactActions.kt
package com.fieldflow.feature.business.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ContactActions(
    phoneNumber: String?,
    email: String?,
    website: String?,
    location: Pair<Double, Double>,
    onPhoneClick: (String) -> Unit,
    onEmailClick: (String) -> Unit,
    onWebsiteClick: (String) -> Unit,
    onDirectionsClick: (Pair<Double, Double>) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ContactActionButton(
            icon = Icons.Filled.Phone,
            label = "Call",
            enabled = phoneNumber != null,
            onClick = { phoneNumber?.let { onPhoneClick(it) } }
        )
        
        ContactActionButton(
            icon = Icons.Filled.Email,
            label = "Email",
            enabled = email != null,
            onClick = { email?.let { onEmailClick(it) } }
        )
        
        ContactActionButton(
            icon = Icons.Filled.Language,
            label = "Website",
            enabled = website != null,
            onClick = { website?.let { onWebsiteClick(it) } }
        )
        
        ContactActionButton(
            icon = Icons.Filled.Directions,
            label = "Directions",
            enabled = true,
            onClick = { onDirectionsClick(location) }
        )
    }
}

@Composable
private fun ContactActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(icon, label, Modifier.size(24.dp))
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            textAlign = TextAlign.Center
        )
    }
}