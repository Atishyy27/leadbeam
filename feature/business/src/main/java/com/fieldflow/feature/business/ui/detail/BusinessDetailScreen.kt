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
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldflow.feature.business.domain.model.BusinessDetail
import com.fieldflow.feature.business.ui.components.BusinessDetailHeader
import com.fieldflow.feature.business.ui.components.ConfidenceIndicator
import com.fieldflow.feature.business.ui.components.ContactActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: BusinessDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BusinessDetailEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is BusinessDetailEvent.NavigateToDialer -> {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${event.phoneNumber}")))
                }
                is BusinessDetailEvent.NavigateToEmail -> {
                    context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${event.email}")))
                }
                is BusinessDetailEvent.NavigateToWebsite -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(event.url)))
                }
                is BusinessDetailEvent.NavigateToMaps -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:${event.lat},${event.long}")))
                }
                BusinessDetailEvent.NavigateBack -> onNavigateBack()
            }
        }
    }
    
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
                    IconButton(onClick = { viewModel.onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is BusinessDetailUiState.Success -> {
                            IconButton(onClick = { viewModel.toggleFavorite() }) {
                                Icon(
                                    imageVector = if (state.business.isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "Favorite",
                                    tint = if (state.business.isFavorite) Color(0xFFFFB800) else LocalContentColor.current
                                )
                            }
                            IconButton(onClick = { viewModel.toggleHidden() }) {
                                Icon(
                                    imageVector = if (state.business.isHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Hide"
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
                    onPhoneClick = { viewModel.onPhoneClick(it) },
                    onEmailClick = { viewModel.onEmailClick(it) },
                    onWebsiteClick = { viewModel.onWebsiteClick(it) },
                    onDirectionsClick = { viewModel.onDirectionsClick(it.first, it.second) },
                    modifier = Modifier.padding(padding)
                )
            }
            is BusinessDetailUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Error, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Text(state.message)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BusinessDetailContent(
    business: BusinessDetail,
    onPhoneClick: (String) -> Unit,
    onEmailClick: (String) -> Unit,
    onWebsiteClick: (String) -> Unit,
    onDirectionsClick: (Pair<Double, Double>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        BusinessDetailHeader(business)
        Divider()
        ContactActions(
            phoneNumber = business.phonePrimary,
            email = business.email,
            website = business.website,
            location = business.lat to business.long,
            onPhoneClick = onPhoneClick,
            onEmailClick = onEmailClick,
            onWebsiteClick = onWebsiteClick,
            onDirectionsClick = onDirectionsClick
        )
        Divider()
        
        // Address
        Column(Modifier.padding(16.dp)) {
            Text("Address", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(business.addressFull)
            Text("${business.city}, ${business.state} ${business.postalCode ?: ""}", style = MaterialTheme.typography.bodySmall)
        }
        
        Divider()
        
        // Description
        business.description?.let {
            Column(Modifier.padding(16.dp)) {
                Text("About", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(it)
            }
            Divider()
        }
        
        // Confidence
        ConfidenceIndicator(
            confidence = business.overallConfidence,
            completeness = business.dataCompleteness,
            sources = business.sourceCount,
            modifier = Modifier.padding(16.dp)
        )
    }
}