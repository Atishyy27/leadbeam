package com.fieldflow.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_dest")
    object MapDashboard : Screen("map_dashboard_dest")
}