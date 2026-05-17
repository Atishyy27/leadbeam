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
import com.fieldflow.feature.route.ui.create.CreateRouteScreen
import com.fieldflow.feature.route.ui.detail.RouteDetailScreen
import com.fieldflow.feature.route.ui.execution.RouteExecutionScreen
import com.fieldflow.feature.route.ui.list.RoutesListScreen
import com.fieldflow.feature.map.ui.MapScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Map : Screen("map")
    object BusinessList : Screen("business_list")
    object BusinessDetail : Screen("business/{businessId}") {
        fun createRoute(businessId: String) = "business/$businessId"
    }
    object RoutesList : Screen("routes")
    object CreateRoute : Screen("create_route")
    object RouteDetail : Screen("route/{routeId}") {
        fun createRoute(routeId: String) = "route/$routeId"
    }
    object RouteExecution : Screen("route/execution")
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
        // --- AUTH ---
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        // --- MAP ---
        composable(Screen.Map.route) {
            MapScreen(
                onBusinessClick = { businessId ->
                    navController.navigate(Screen.BusinessDetail.createRoute(businessId))
                },
                onNavigateToList = {
                    navController.navigate(Screen.BusinessList.route)
                },
                onNavigateToRoutes = {
                    navController.navigate(Screen.RoutesList.route)
                }
            )
        }
        
        // --- BUSINESS DIRECTORY ---
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
        
        // --- ROUTING (BLOCK 2C & PHASE 3) ---
        composable(Screen.RoutesList.route) {
            RoutesListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateRoute.route)
                },
                onRouteClick = { routeId ->
                    // Now correctly navigates to RouteDetailScreen
                    navController.navigate(Screen.RouteDetail.createRoute(routeId)) 
                }
            )
        }
        
        composable(Screen.CreateRoute.route) {
            CreateRouteScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBusinessPicker = {
                    navController.navigate(Screen.BusinessList.route)
                }
            )
        }
        
        composable(
            route = Screen.RouteDetail.route,
            arguments = listOf(navArgument("routeId") { type = NavType.StringType })
        ) {
            RouteDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.RouteExecution.route) {
            RouteExecutionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}