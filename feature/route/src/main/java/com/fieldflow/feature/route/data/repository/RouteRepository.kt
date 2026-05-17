// feature/route/src/main/java/com/fieldflow/feature/route/data/repository/RouteRepository.kt
package com.fieldflow.feature.route.data.repository

import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.dao.RouteDao
import com.fieldflow.core.database.entities.RouteEntity
import com.fieldflow.core.database.entities.RouteStopEntity
import com.fieldflow.core.network.ApiService
import com.fieldflow.feature.business.data.mapper.toDomain
import com.fieldflow.feature.route.data.model.OptimizationResult
import com.fieldflow.feature.route.data.model.Route
import com.fieldflow.feature.route.data.model.RouteStop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Result<Nothing>()
}

@Singleton
class RouteRepository @Inject constructor(
    private val routeDao: RouteDao,
    private val businessDao: BusinessDao,
    private val apiService: ApiService
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
    
    fun getRouteById(routeId: String): Flow<Route?> {
        return routeDao.getRouteWithStopsById(routeId).map { routeWithStops ->
            routeWithStops?.let {
                Route(
                    id = it.route.id,
                    name = it.route.name,
                    date = it.route.date,
                    createdAt = it.route.createdAt,
                    isCompleted = it.route.isCompleted,
                    totalDistance = it.route.totalDistance,
                    totalDuration = it.route.totalDuration,
                    stops = it.stops.sortedBy { stop -> stop.stop.orderIndex }.map { stopWithBusiness ->
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
    
    // NEW: Optimize route
    suspend fun optimizeRoute(routeId: String): Result<OptimizationResult> {
        return try {
            // Get current route state
            val currentRoute = getRouteById(routeId).first() 
                ?: return Result.Error("Route not found")
            
            // Call API
            val response = apiService.optimizeRoute(routeId)
            val data = response.data
            
            // Build optimization result
            val optimizedStops = data.optimizedStops.sortedBy { it.orderIndex }.mapNotNull { optimizedStop ->
                val business = businessDao.getById(optimizedStop.businessId) ?: return@mapNotNull null
                val originalStop = currentRoute.stops.find { it.business.leadbeamId == optimizedStop.businessId }
                
                RouteStop(
                    id = originalStop?.id ?: "stop_${UUID.randomUUID()}",
                    routeId = routeId,
                    orderIndex = optimizedStop.orderIndex,
                    isVisited = originalStop?.isVisited ?: false,
                    visitedAt = originalStop?.visitedAt,
                    notes = originalStop?.notes,
                    business = business.toDomain()
                )
            }
            
            val result = OptimizationResult(
                routeId = routeId,
                originalOrder = currentRoute.stops,
                optimizedOrder = optimizedStops,
                originalDistance = data.originalDistance,
                optimizedDistance = data.optimizedDistance,
                originalDuration = data.originalDuration,
                optimizedDuration = data.optimizedDuration
            )
            
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error("Failed to optimize route: ${e.message}", e)
        }
    }
    
    // NEW: Accept optimized route
    suspend fun acceptOptimization(optimizationResult: OptimizationResult) {
        val stops = optimizationResult.optimizedOrder.map { stop ->
            RouteStopEntity(
                id = stop.id,
                routeId = optimizationResult.routeId,
                businessId = stop.business.leadbeamId,
                orderIndex = stop.orderIndex,
                isVisited = stop.isVisited,
                visitedAt = stop.visitedAt,
                notes = stop.notes
            )
        }
        
        // Update route with optimized distances/durations
        val route = routeDao.getAllRoutesWithStops().first()
            .find { it.route.id == optimizationResult.routeId }?.route
        
        if (route != null) {
            routeDao.updateRoute(
                route.copy(
                    totalDistance = optimizationResult.optimizedDistance,
                    totalDuration = optimizationResult.optimizedDuration
                )
            )
        }
        
        routeDao.insertStops(stops)
    }
}