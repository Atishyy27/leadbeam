// feature/route/src/main/java/com/fieldflow/feature/route/data/repository/RouteRepository.kt
package com.fieldflow.feature.route.data.repository

import com.fieldflow.core.database.dao.RouteDao
import com.fieldflow.core.database.entities.RouteEntity
import com.fieldflow.core.database.entities.RouteStopEntity
import com.fieldflow.feature.business.data.mapper.toDomain
import com.fieldflow.feature.route.data.model.Route
import com.fieldflow.feature.route.data.model.RouteStop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepository @Inject constructor(
    private val routeDao: RouteDao
) {
    
    fun getAllRoutes(): Flow<List<Route>> {
        return routeDao.getAllRoutesWithStops().map { routesWithStops ->
            routesWithStops.map { routeWithStops ->
                Route(
                    id = routeWithStops.route.id,
                    name = routeWithStops.route.name,
                    date = routeWithStops.route.date,
                    createdAt = routeWithStops.route.createdAt,
                    isCompleted = routeWithStops.route.isCompleted,
                    totalDistance = routeWithStops.route.totalDistance,
                    totalDuration = routeWithStops.route.totalDuration,
                    stops = routeWithStops.stops.sortedBy { it.stop.orderIndex }.map { stopWithBusiness ->
                        RouteStop(
                            id = stopWithBusiness.stop.id,
                            routeId = stopWithBusiness.stop.routeId,
                            orderIndex = stopWithBusiness.stop.orderIndex,
                            isVisited = stopWithBusiness.stop.isVisited,
                            visitedAt = stopWithBusiness.stop.visitedAt,
                            notes = stopWithBusiness.stop.notes,
                            business = stopWithBusiness.business.toDomain()
                        )
                    }
                )
            }
        }
    }
    
    suspend fun createRoute(name: String, date: String, businessIds: List<String>) {
        val routeId = "route_${UUID.randomUUID()}"
        val route = RouteEntity(
            id = routeId,
            name = name,
            date = date,
            createdAt = System.currentTimeMillis(),
            isCompleted = false
        )
        
        val stops = businessIds.mapIndexed { index, businessId ->
            RouteStopEntity(
                id = "stop_${UUID.randomUUID()}",
                routeId = routeId,
                businessId = businessId,
                orderIndex = index,
                isVisited = false
            )
        }
        
        routeDao.insertRouteWithStops(route, stops)
    }
    
    suspend fun deleteRoute(routeId: String) {
        routeDao.deleteRouteById(routeId)
    }
    
    suspend fun updateStopOrder(routeId: String, businessIds: List<String>) {
        val stops = businessIds.mapIndexed { index, businessId ->
            RouteStopEntity(
                id = "stop_${UUID.randomUUID()}",
                routeId = routeId,
                businessId = businessId,
                orderIndex = index,
                isVisited = false
            )
        }
        routeDao.insertStops(stops)
    }
}