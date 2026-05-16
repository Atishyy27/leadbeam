package com.fieldflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fieldflow.feature.auth.ui.LoginScreen
import com.fieldflow.feature.auth.ui.LoginViewModel

@Composable
fun FieldFlowNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccessNavigation = {
                    navController.navigate(Screen.MapDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.MapDashboard.route) {
            // Import: androidx.hilt.navigation.compose.hiltViewModel
            val mapViewModel: com.fieldflow.feature.map.ui.MapViewModel = hiltViewModel()
            
            com.fieldflow.feature.map.ui.MapScreen(
                viewModel = mapViewModel
            )
        }
    }
}