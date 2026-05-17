// core/database/src/main/java/com/fieldflow/core/database/dao/RouteDao.kt
package com.fieldflow.core.database.dao

import androidx.room.*
import com.fieldflow.core.database.entity.BusinessEntity
import com.fieldflow.core.database.entity.RouteEntity
import com.fieldflow.core.database.entity.RouteStopEntity
import kotlinx.coroutines.flow.Flow

data class RouteWithStops(
    @Embedded val route: RouteEntity,
    @Relation(
        entity = RouteStopEntity::class,
        parentColumn = "id",
        entityColumn = "route_id"
    )
    val stops: List<RouteStopWithBusiness>
)

data class RouteStopWithBusiness(
    @Embedded val stop: RouteStopEntity,
    @Relation(
        parentColumn = "business_id",
        entityColumn = "leadbeam_id"
    )
    val business: BusinessEntity
)

@Dao
interface RouteDao {
    
    // --- INSERT & UPDATE ---
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStops(stops: List<RouteStopEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouteStop(stop: RouteStopEntity)
    
    @Transaction
    suspend fun insertRouteWithStops(route: RouteEntity, stops: List<RouteStopEntity>) {
        insertRoute(route)
        insertStops(stops)
    }
    
    @Update
    suspend fun updateRoute(route: RouteEntity)
    
    @Update
    suspend fun updateStop(stop: RouteStopEntity)
    
    // --- DELETE ---
    
    @Delete
    suspend fun deleteRoute(route: RouteEntity)
    
    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRouteById(routeId: String)
    
    @Delete
    suspend fun deleteRouteStop(stop: RouteStopEntity)
    
    // --- FETCH ROUTES ---
    
    @Transaction
    @Query("SELECT * FROM routes ORDER BY date DESC, created_at DESC")
    fun getAllRoutesWithStops(): Flow<List<RouteWithStops>>
    
    @Transaction
    @Query("SELECT * FROM routes WHERE id = :routeId")
    fun getRouteWithStopsById(routeId: String): Flow<RouteWithStops?>
    
    @Transaction
    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRouteWithStopsByIdOnce(routeId: String): RouteWithStops?
    
    @Query("SELECT * FROM routes ORDER BY date DESC, created_at DESC")
    fun getAllRoutes(): Flow<List<RouteEntity>>
    
    // --- FETCH STOPS ---
    
    @Query("SELECT * FROM route_stops WHERE route_id = :routeId ORDER BY order_index ASC")
    fun getStopsForRoute(routeId: String): Flow<List<RouteStopEntity>>

    @Query("SELECT * FROM route_stops WHERE route_id = :routeId ORDER BY order_index ASC")
    suspend fun getStopsForRouteSync(routeId: String): List<RouteStopEntity>
    
    @Query("SELECT COUNT(*) FROM route_stops WHERE route_id = :routeId")
    suspend fun getStopCount(routeId: String): Int

    @Query("SELECT COUNT(*) FROM route_stops WHERE route_id = :routeId")
    suspend fun getStopCountForRoute(routeId: String): Int
    
    // --- ACTIVE ROUTE MANAGEMENT ---
    
    // Legacy support for older viewmodels
    @Query("SELECT * FROM routes WHERE status = 'active' LIMIT 1")
    suspend fun getActiveRouteEntity(): RouteEntity?
    
    @Transaction
    @Query("SELECT * FROM routes WHERE is_active = 1 LIMIT 1")
    fun getActiveRoute(): Flow<RouteWithStops?>
    
    @Transaction
    @Query("SELECT * FROM routes WHERE is_active = 1 LIMIT 1")
    suspend fun getActiveRouteOnce(): RouteWithStops?
    
    @Transaction
    suspend fun setActiveRoute(routeId: String) {
        // Deactivate all routes
        clearActiveRoutes()
        // Activate target route
        val route = getRouteWithStopsByIdOnce(routeId)?.route ?: return
        updateRoute(
            route.copy(
                isActive = true,
                status = "active",
                startedAt = route.startedAt ?: System.currentTimeMillis()
            )
        )
    }
    
    @Query("UPDATE routes SET is_active = 0")
    suspend fun clearActiveRoutes()
    
    @Query("UPDATE routes SET status = :status WHERE id = :routeId")
    suspend fun updateRouteStatus(routeId: String, status: String)
    
    @Query("""
        UPDATE routes 
        SET is_completed = 1, 
            status = 'completed', 
            completed_at = :completedAt,
            is_active = 0
        WHERE id = :routeId
    """)
    suspend fun completeRoute(routeId: String, completedAt: Long)
}