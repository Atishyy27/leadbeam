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
    
    // --- V1: Active Route Management (Used by MapViewModel) ---
    
    @Query("SELECT * FROM routes WHERE status = 'active' LIMIT 1")
    suspend fun getActiveRoute(): RouteEntity?

    @Query("SELECT COUNT(*) FROM route_stops WHERE route_id = :routeId")
    suspend fun getStopCountForRoute(routeId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouteStop(stop: RouteStopEntity)

    @Delete
    suspend fun deleteRouteStop(stop: RouteStopEntity)

    // A synchronous list return for older MapViewModel logic
    @Query("SELECT * FROM route_stops WHERE route_id = :routeId ORDER BY order_index ASC")
    suspend fun getStopsForRouteSync(routeId: String): List<RouteStopEntity>

    // --- V2: Advanced Route & List Management ---
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStops(stops: List<RouteStopEntity>)
    
    @Transaction
    suspend fun insertRouteWithStops(route: RouteEntity, stops: List<RouteStopEntity>) {
        insertRoute(route)
        insertStops(stops)
    }
    
    @Update
    suspend fun updateRoute(route: RouteEntity)
    
    @Delete
    suspend fun deleteRoute(route: RouteEntity)
    
    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRouteById(routeId: String)
    
    @Transaction
    @Query("SELECT * FROM routes ORDER BY date DESC, created_at DESC")
    fun getAllRoutesWithStops(): Flow<List<RouteWithStops>>
    
    @Transaction
    @Query("SELECT * FROM routes WHERE id = :routeId")
    fun getRouteWithStopsById(routeId: String): Flow<RouteWithStops?>
    
    @Query("SELECT * FROM routes ORDER BY date DESC, created_at DESC")
    fun getAllRoutes(): Flow<List<RouteEntity>>
    
    // A reactive Flow return for the new RouteList UI
    @Query("SELECT * FROM route_stops WHERE route_id = :routeId ORDER BY order_index ASC")
    fun getStopsForRoute(routeId: String): Flow<List<RouteStopEntity>>
    
    @Query("SELECT COUNT(*) FROM route_stops WHERE route_id = :routeId")
    suspend fun getStopCount(routeId: String): Int
}