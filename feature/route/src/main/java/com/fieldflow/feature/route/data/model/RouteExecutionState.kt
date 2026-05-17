// feature/route/src/main/java/com/fieldflow/feature/route/data/model/RouteExecutionState.kt
package com.fieldflow.feature.route.data.model

data class RouteExecutionState(
    val route: Route,
    val currentStopIndex: Int,
    val visitedStopIds: Set<String>,
    val lastVisitedStopId: String? = null
) {
    val currentStop: RouteStop?
        get() = route.stops.getOrNull(currentStopIndex)
    
    val nextUnvisitedStop: RouteStop?
        get() = route.stops.firstOrNull { !visitedStopIds.contains(it.id) }
    
    val isCompleted: Boolean
        get() = visitedStopIds.size == route.stops.size
    
    val progress: Float
        get() = if (route.stops.isEmpty()) 0f 
                else visitedStopIds.size.toFloat() / route.stops.size
    
    val progressText: String
        get() = "${visitedStopIds.size}/${route.stops.size} stops"
}