// feature/route/src/main/java/com/fieldflow/feature/route/data/model/OptimizationResult.kt
package com.fieldflow.feature.route.data.model

import com.fieldflow.feature.business.domain.model.BusinessDetail

data class OptimizationResult(
    val routeId: String,
    val originalOrder: List<RouteStop>,
    val optimizedOrder: List<RouteStop>,
    val originalDistance: Double,
    val optimizedDistance: Double,
    val originalDuration: Int,
    val optimizedDuration: Int
) {
    val distanceSaved: Double
        get() = originalDistance - optimizedDistance
    
    val timeSaved: Int
        get() = originalDuration - optimizedDuration
    
    val distanceSavedPercent: Int
        get() = if (originalDistance > 0) {
            ((distanceSaved / originalDistance) * 100).toInt()
        } else 0
    
    val timeSavedPercent: Int
        get() = if (originalDuration > 0) {
            ((timeSaved.toDouble() / originalDuration) * 100).toInt()
        } else 0
}

data class OptimizedStopInfo(
    val businessId: String,
    val orderIndex: Int,
    val name: String,
    val lat: Double,
    val long: Double
)