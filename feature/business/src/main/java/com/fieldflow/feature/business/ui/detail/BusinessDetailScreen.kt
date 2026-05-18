// feature/business/src/main/java/com/fieldflow/feature/business/ui/detail/BusinessDetailScreen.kt
package com.fieldflow.feature.business.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldflow.feature.business.domain.model.BusinessDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: BusinessDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    when (val state = uiState) {
                        is BusinessDetailUiState.Success -> Text(state.business.name)
                        else -> Text("Business Details")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is BusinessDetailUiState.Success -> {
                            IconButton(onClick = { /* Toggle favorite */ }) {
                                Icon(
                                    imageVector = if (state.business.isFavorite) 
                                        Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "Favorite",
                                    tint = if (state.business.isFavorite) 
                                        Color(0xFFFFB800) else LocalContentColor.current
                                )
                            }
                        }
                        else -> {}
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (val state = uiState) {
            is BusinessDetailUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is BusinessDetailUiState.Success -> {
                BusinessDetailContent(
                    business = state.business,
                    modifier = Modifier.padding(padding)
                )
            }
            is BusinessDetailUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Error, null, Modifier.size(64.dp), 
                            tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Text(state.message)
                    }
                }
            }
        }
    }
}

@Composable
private fun BusinessDetailContent(
    business: BusinessDetail,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Column(modifier.verticalScroll(rememberScrollState())) {
        
        // Header Section
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = business.name,
                style = MaterialTheme.typography.headlineLarge
            )
            
            Spacer(Modifier.height(4.dp))
            
            Text(
                text = business.categoryDisplay,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            // P1 FIX: Verified Badge
            if (business.overallConfidence > 0.8) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Verified,
                        contentDescription = "Verified",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Verified Business",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF1976D2)
                    )
                }
            }
            
            // Rating
            business.rating?.let { rating ->
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, 
                        tint = Color(0xFFFFB800), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        String.format("%.1f", rating),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        
        HorizontalDivider()
        
        // P1 FIX: Contact Actions with Intents
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Contact", style = MaterialTheme.typography.titleMedium)
            
            // Phone
            business.phonePrimary?.let { phone ->
                OutlinedCard(
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Phone, "Call", 
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text(phone)
                    }
                }
            }
            
            // Email
            business.email?.let { email ->
                OutlinedCard(
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Email, "Email", 
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text(email)
                    }
                }
            }
            
            // Website
            business.website?.let { website ->
                OutlinedCard(
                    onClick = {
                        val url = if (!website.startsWith("http")) {
                            "https://$website"
                        } else website
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Language, "Website", 
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text(website, maxLines = 1)
                    }
                }
            }
            
            // Directions
            OutlinedCard(
                onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW, 
                            Uri.parse("geo:${business.lat},${business.long}")
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Directions, "Directions", 
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Text("Get Directions")
                }
            }
        }
        
        HorizontalDivider()
        
        // P1 FIX: Use address_full from API (no manual concatenation)
        Column(Modifier.padding(16.dp)) {
            Text("Address", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            
            // Use address_full directly from API
            if (business.addressFull.isNotBlank()) {
                Text(
                    text = business.addressFull,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // City, State, Zip on separate line
            val cityStateZip = listOfNotNull(
                business.city.takeIf { it.isNotBlank() },
                business.state.takeIf { it.isNotBlank() },
                business.postalCode?.takeIf { it.isNotBlank() }
            ).joinToString(", ")
            
            if (cityStateZip.isNotEmpty()) {
                Text(
                    text = cityStateZip,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        HorizontalDivider()
        
        // P1 FIX: Operating Hours Display
        business.operatingHours?.let { hours ->
            if (hours.isNotBlank()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Hours", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    
                    // Parse JSON format if needed, or display as-is
                    Text(
                        text = hours,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                HorizontalDivider()
            }
        }
        
        // Description
        business.description?.let { desc ->
            if (desc.isNotBlank()) {
                Column(Modifier.padding(16.dp)) {
                    Text("About", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(desc, style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider()
            }
        }
        
        // Confidence Indicator (already working)
        Column(Modifier.padding(16.dp)) {
            Text("Data Quality", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { business.overallConfidence.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${(business.overallConfidence * 100).toInt()}% confidence",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}