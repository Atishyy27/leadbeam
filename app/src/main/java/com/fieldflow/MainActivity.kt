// app/src/main/java/com/fieldflow/MainActivity.kt
package com.fieldflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.fieldflow.core.common.connectivity.NetworkMonitor
import com.fieldflow.core.sync.SyncManager
import com.fieldflow.core.sync.workers.CacheCleanupWorker
import com.fieldflow.feature.auth.domain.usecase.CheckSessionUseCase
import com.fieldflow.navigation.FieldFlowNavHost
import com.fieldflow.navigation.Screen
import com.fieldflow.ui.components.OfflineBanner
import com.fieldflow.core.ui.theme.FieldFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager,
    private val workManager: WorkManager
) : ViewModel() {
    
    val isOnline = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online) {
                    syncManager.scheduleSync()
                }
            }
        }
        scheduleCacheCleanup()
    }
    
    private fun scheduleCacheCleanup() {
        val cleanupRequest = PeriodicWorkRequestBuilder<CacheCleanupWorker>(
            24, TimeUnit.HOURS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()
        
        workManager.enqueueUniquePeriodicWork(
            "cache_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var checkSessionUseCase: CheckSessionUseCase
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FieldFlowTheme {
                val navController = rememberNavController()
                var startDestination by remember { mutableStateOf<String?>(null) }
                val isOnline by viewModel.isOnline.collectAsState()

                LaunchedEffect(Unit) {
                    val isSessionActive = checkSessionUseCase()
                    startDestination = if (isSessionActive) {
                        Screen.Map.route
                    } else {
                        Screen.Login.route
                    }
                }
                
                val finalDestination = startDestination
                if (finalDestination != null) {
                    AppScaffoldWithBottomNav(
                        navController = navController,
                        startDestination = finalDestination,
                        isOnline = isOnline
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun AppScaffoldWithBottomNav(
    navController: NavHostController,
    startDestination: String,
    isOnline: Boolean
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Hide bottom bar on login, business detail, route detail, route execution, and create route
    val showBottomBar = currentRoute != null &&
        currentRoute != Screen.Login.route &&
        !currentRoute.startsWith("business/") &&
        currentRoute != Screen.CreateRoute.route &&
        !currentRoute.startsWith("route/execution") &&
        !currentRoute.startsWith("route/{routeId}")  // hide on detail too for cleaner UX
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    // Map Tab
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Map, contentDescription = "Map") },
                        label = { Text("Map") },
                        selected = currentRoute == Screen.Map.route,
                        onClick = {
                            navController.navigate(Screen.Map.route) {
                                popUpTo(Screen.Map.route) { inclusive = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    
                    // Businesses Tab
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Business, contentDescription = "Businesses") },
                        label = { Text("Businesses") },
                        selected = currentRoute == Screen.BusinessList.route,
                        onClick = {
                            navController.navigate(Screen.BusinessList.route) {
                                popUpTo(Screen.Map.route)
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    
                    // Routes Tab — FIXED: Screen.RoutesList.route (not Screen.RouteList)
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Route, contentDescription = "Routes") },
                        label = { Text("Routes") },
                        selected = currentRoute == Screen.RoutesList.route,
                        onClick = {
                            navController.navigate(Screen.RoutesList.route) {
                                popUpTo(Screen.Map.route)
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OfflineBanner(isOffline = !isOnline)
            
            FieldFlowNavHost(
                navController = navController,
                startDestination = startDestination
            )
        }
    }
}