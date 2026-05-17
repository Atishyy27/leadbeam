// feature/route/src/main/java/com/fieldflow/feature/route/data/repository/RouteRepository.kt
package com.fieldflow.feature.route.data.repository

import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.dao.RouteDao
import com.fieldflow.core.database.entities.RouteEntity
import com.fieldflow.core.database.entities.RouteStopEntity
import com.fieldflow.core.network.api.ApiService
import com.fieldflow.core.sync.SyncManager
import com.fieldflow.feature.business.data.mapper.toDomain
import com.fieldflow.feature.route.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
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
    private val apiService: ApiService,
    private val syncManager: SyncManager
) {
    
    // --- BASIC ROUTE CRUD (OFFLINE-FIRST) ---

    fun getAllRoutes(): Flow<List<Route>> {
        return routeDao.getAllRoutesWithStops().map { routesWithStops ->
            routesWithStops.map { it.toRoute() }
        }
    }
    
    fun getRouteById(routeId: String): Flow<Route?> {
        return routeDao.getRouteWithStopsById(routeId).map { it?.toRoute() }
    }
    
    suspend fun createRoute(name: String, date: String, businessIds: List<String>) {
        val routeId = "route_${UUID.randomUUID()}"
        val route = RouteEntity(
            id = routeId,
            name = name,
            date = date,
            createdAt = System.currentTimeMillis(),
            isCompleted = false,
            status = "planned"
        )
        
        val stops = businessIds.mapIndexed { index, businessId ->
            val business = businessDao.getById(businessId)
            RouteStopEntity(
                id = "stop_${UUID.randomUUID()}", // Client-side UUID for offline sync safety
                routeId = routeId,
                businessId = businessId,
                businessName = business?.name ?: "Unknown Business",
                lat = business?.lat ?: 0.0,
                long = business?.long ?: 0.0,
                orderIndex = index,
                isVisited = false
            )
        }
        
        routeDao.insertRouteWithStops(route, stops)
    }
    
    suspend fun deleteRoute(routeId: String) {
        routeDao.deleteRouteById(routeId)
    }
    
    // --- ROUTE OPTIMIZATION ---

    suspend fun optimizeRoute(routeId: String): Result<OptimizationResult> {
        return try {
            val currentRoute = getRouteById(routeId).first() 
                ?: return Result.Error("Route not found in local cache")
            
            val response = apiService.optimizeRoute(routeId)
            if (!response.isSuccessful) {
                return Result.Error("Failed to optimize route: ${response.code()} - ${response.message()}")
            }
            
            val data = response.body()?.data ?: return Result.Error("API returned empty data")
            
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
    
    suspend fun acceptOptimization(optimizationResult: OptimizationResult) {
        val stops = optimizationResult.optimizedOrder.map { stop ->
            RouteStopEntity(
                id = stop.id,
                routeId = optimizationResult.routeId,
                businessId = stop.business.leadbeamId,
                businessName = stop.business.name,
                lat = stop.business.lat,
                long = stop.business.long,
                orderIndex = stop.orderIndex,
                isVisited = stop.isVisited,
                visitedAt = stop.visitedAt,
                notes = stop.notes
            )
        }
        
        val route = routeDao.getRouteWithStopsByIdOnce(optimizationResult.routeId)?.route
        
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
    
    // --- ACTIVE ROUTE EXECUTION & SYNC ---

    fun getActiveRoute(): Flow<RouteExecutionState?> {
        return routeDao.getActiveRoute().map { routeWithStops ->
            routeWithStops?.let {
                val route = it.toRoute()
                val visitedStops = route.stops.filter { stop -> stop.isVisited }.map { stop -> stop.id }.toSet()
                val currentIndex = route.stops.indexOfFirst { stop -> !stop.isVisited }.coerceAtLeast(0)
                val lastVisited = route.stops.filter { stop -> stop.isVisited }
                    .maxByOrNull { stop -> stop.visitedAt ?: 0L }?.id
                
                RouteExecutionState(
                    route = route,
                    currentStopIndex = currentIndex,
                    visitedStopIds = visitedStops,
                    lastVisitedStopId = lastVisited
                )
            }
        }
    }

    suspend fun startRouteExecution(routeId: String) {
        routeDao.setActiveRoute(routeId)
    }
    
    // OPTIMISTIC UPDATE: Mark stop visited
    suspend fun markStopVisited(stopId: String) {
        val activeRoute = routeDao.getActiveRouteOnce() ?: return
        val stop = activeRoute.stops.find { it.stop.id == stopId }?.stop ?: return
        
        // 1. Update local DB immediately
        val visitedAt = System.currentTimeMillis()
        routeDao.updateStop(
            stop.copy(
                isVisited = true,
                visitedAt = visitedAt
            )
        )
        
        // 2. Queue background sync
        val data = JSONObject().apply {
            put("isVisited", true)
            put("visitedAt", visitedAt)
        }.toString()
        
        syncManager.enqueueSyncItem(
            entityType = "route_stop",
            entityId = stopId,
            action = "mark_visited",
            data = data
        )
        
        // 3. Check if all stops visited
        val allStops = activeRoute.stops.map { it.stop }
        if (allStops.all { it.isVisited || it.id == stopId }) {
            completeRoute(activeRoute.route.id)
        }
    }
    
    // OPTIMISTIC UPDATE: Undo visit
    suspend fun undoLastVisit(stopId: String) {
        val activeRoute = routeDao.getActiveRouteOnce() ?: return
        val stop = activeRoute.stops.find { it.stop.id == stopId }?.stop ?: return
        
        // 1. Update local DB immediately
        routeDao.updateStop(
            stop.copy(
                isVisited = false,
                visitedAt = null
            )
        )
        
        // 2. Queue background sync
        val data = JSONObject().apply {
            put("isVisited", false)
        }.toString()
        
        syncManager.enqueueSyncItem(
            entityType = "route_stop",
            entityId = stopId,
            action = "undo_visit",
            data = data
        )
    }
    
    suspend fun pauseRoute(routeId: String) {
        routeDao.updateRouteStatus(routeId, "paused")
    }
    
    suspend fun resumeRoute(routeId: String) {
        routeDao.setActiveRoute(routeId)
    }
    
    // OPTIMISTIC UPDATE: Complete route
    suspend fun completeRoute(routeId: String) {
        val completedAt = System.currentTimeMillis()
        
        // 1. Update local DB immediately
        routeDao.completeRoute(routeId, completedAt)
        
        // 2. Queue background sync
        val data = JSONObject().apply {
            put("completedAt", completedAt)
        }.toString()
        
        syncManager.enqueueSyncItem(
            entityType = "route_completed",
            entityId = routeId,
            action = "complete",
            data = data
        )
    }
    
    // --- MAPPERS ---

    private fun com.fieldflow.core.database.dao.RouteWithStops.toRoute(): Route {
        return Route(
            id = route.id,
            name = route.name,
            date = route.date,
            createdAt = route.createdAt,
            isCompleted = route.isCompleted,
            totalDistance = route.totalDistance,
            totalDuration = route.totalDuration,
            stops = stops.sortedBy { it.stop.orderIndex }.map { stopWithBusiness ->
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