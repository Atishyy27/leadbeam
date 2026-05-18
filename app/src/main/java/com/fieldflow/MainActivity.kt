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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
        // Trigger sync when coming online
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online) {
                    syncManager.scheduleSync()
                }
            }
        }
        
        // Schedule periodic cache cleanup
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

                // Cold boot lifecycle verification
                LaunchedEffect(Unit) {
                    val isSessionActive = checkSessionUseCase()
                    startDestination = if (isSessionActive) {
                        Screen.Map.route // Using Map.route as defined in your merged NavGraph
                    } else {
                        Screen.Login.route
                    }
                }
                
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    Column(modifier = Modifier.padding(padding)) {
                        
                        OfflineBanner(isOffline = !isOnline)
                        
                        val finalDestination = startDestination
                        if (finalDestination != null) {
                            FieldFlowNavHost(
                                navController = navController,
                                startDestination = finalDestination
                            )
                        } else {
                            // Centered system loader displayed while thread validation finishes
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
    }
}