package com.fieldflow.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fieldflow.feature.auth.ui.LoginScreen
import com.fieldflow.feature.business.ui.detail.BusinessDetailScreen
import com.fieldflow.feature.business.ui.list.BusinessListScreen
import com.fieldflow.feature.map.ui.MapScreen
import com.fieldflow.feature.route.ui.create.CreateRouteScreen
import com.fieldflow.feature.route.ui.detail.RouteDetailScreen
import com.fieldflow.feature.route.ui.execution.RouteExecutionScreen
import com.fieldflow.feature.route.ui.list.RoutesListScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Map : Screen("map")
    object BusinessList : Screen("business_list")
    
    object BusinessDetail : Screen("business/{businessId}") {
        fun createRoute(businessId: String) = "business/$businessId"
    }
    
    object RoutesList : Screen("routes")
    object CreateRoute : Screen("create_route")
    
    object RouteDetail : Screen("route_detail/{routeId}") {
        fun createRoute(routeId: String) = "route_detail/$routeId"
    }
    
    // RouteExecution now correctly takes a routeId argument
    object RouteExecution : Screen("route_execution/{routeId}") {
        fun createRoute(routeId: String) = "route_execution/$routeId"
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
        // --- AUTH ---
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = hiltViewModel(),
                onLoginSuccessNavigation = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        // --- MAP ---
        composable(Screen.Map.route) {
            MapScreen(
                viewModel = hiltViewModel(),
                onNavigateToList = {
                    navController.navigate(Screen.BusinessList.route)
                },
                onBusinessClick = { businessId ->
                    navController.navigate(Screen.BusinessDetail.createRoute(businessId))
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
        
        // --- ROUTING ---
        composable(Screen.RoutesList.route) {
            RoutesListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateRoute.route)
                },
                onRouteClick = { routeId ->
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
        
        // FIX: Wired RouteDetail -> RouteExecution with proper routeId passing
        composable(
            route = Screen.RouteDetail.route,
            arguments = listOf(navArgument("routeId") { type = NavType.StringType })
        ) { 
            RouteDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onStartExecution = { routeId ->
                    navController.navigate(Screen.RouteExecution.createRoute(routeId))
                }
            )
        }
        
        // FIX: RouteExecutionScreen destination catches the routeId
        composable(
            route = Screen.RouteExecution.route,
            arguments = listOf(navArgument("routeId") { type = NavType.StringType })
        ) {
            RouteExecutionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}