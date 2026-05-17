// core/database/src/main/java/com/fieldflow/core/database/dao/RouteDao.kt
package com.fieldflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fieldflow.core.database.entity.RouteEntity
import com.fieldflow.core.database.entity.RouteStopEntity

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes WHERE status = 'active' LIMIT 1")
    suspend fun getActiveRoute(): RouteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouteStop(stop: RouteStopEntity)

    @Query("SELECT COUNT(*) FROM route_stops WHERE routeId = :routeId")
    suspend fun getStopCountForRoute(routeId: String): Int
}