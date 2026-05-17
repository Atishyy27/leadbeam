// feature/route/src/main/java/com/fieldflow/feature/route/data/model/Route.kt
package com.fieldflow.feature.route.data.model

import com.fieldflow.feature.business.domain.model.BusinessDetail

data class Route(
    val id: String,
    val name: String,
    val date: String,
    val createdAt: Long,
    val isCompleted: Boolean,
    val stops: List<RouteStop>,
    val totalDistance: Double?,
    val totalDuration: Int?
) {
    val stopCount: Int get() = stops.size
    val visitedCount: Int get() = stops.count { it.isVisited }
}

data class RouteStop(
    val id: String,
    val routeId: String,
    val orderIndex: Int,
    val isVisited: Boolean,
    val visitedAt: Long?,
    val notes: String?,
    val business: BusinessDetail
)