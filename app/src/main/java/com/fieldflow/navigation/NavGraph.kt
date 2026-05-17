// app/src/main/java/com/fieldflow/navigation/NavGraph.kt
package com.fieldflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fieldflow.feature.auth.ui.LoginScreen
import com.fieldflow.feature.business.ui.detail.BusinessDetailScreen
import com.fieldflow.feature.business.ui.list.BusinessListScreen
import com.fieldflow.map.ui.MapScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Map : Screen("map")
    object BusinessList : Screen("business_list")
    object BusinessDetail : Screen("business/{businessId}") {
        fun createRoute(businessId: String) = "business/$businessId"
    }
}

@Composable
fun FieldFlowNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Map.route) {
            MapScreen(
                onBusinessClick = { businessId ->
                    navController.navigate(Screen.BusinessDetail.createRoute(businessId))
                },
                onNavigateToList = {
                    navController.navigate(Screen.BusinessList.route)
                }
            )
        }
        
        composable(Screen.BusinessList.route) {
            BusinessListScreen(
                onNavigateBack = { navController.popBackStack() },
                onBusinessClick = { businessId ->
                    navController.navigate(Screen.BusinessDetail.createRoute(businessId))
                }
            )
        }
        
        composable(
            route = Screen.BusinessDetail.route,
            arguments = listOf(navArgument("businessId") { type = NavType.StringType })
        ) {
            BusinessDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}