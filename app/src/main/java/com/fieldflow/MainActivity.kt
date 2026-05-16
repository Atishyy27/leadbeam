package com.fieldflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.fieldflow.feature.auth.domain.usecase.CheckSessionUseCase
import com.fieldflow.navigation.FieldFlowNavHost
import com.fieldflow.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var checkSessionUseCase: CheckSessionUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            var startDestination by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                // Cold boot lifecycle verification logic checking datastore persistence state
                val isSessionActive = checkSessionUseCase()
                startDestination = if (isSessionActive) {
                    Screen.MapDashboard.route
                } else {
                    Screen.Login.route
                }
            }

            val finalDestination = startDestination
            if (finalDestination != null) {
                FieldFlowNavHost(
                    navController = navController,
                    startDestination = finalDestination
                )
            } else {
                // Centered system loader layout displayed while thread validation finishes
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