// core/database/src/main/java/com/fieldflow/core/database/entity/RouteEntity.kt
package com.fieldflow.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String = "Today's Route", // Added default to prevent crashes with older code
    
    @ColumnInfo(name = "date")
    val date: Long, // Kept as Long because your MapViewModel passes System.currentTimeMillis()
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "status")
    val status: String = "active", // "active" or "completed"
    
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "total_distance")
    val totalDistance: Double? = null,
    
    @ColumnInfo(name = "total_duration")
    val totalDuration: Int? = null
)

@Entity(tableName = "route_stops")
data class RouteStopEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "route_id")
    val routeId: String,
    
    @ColumnInfo(name = "business_id")
    val businessId: String,
    
    @ColumnInfo(name = "business_name")
    val businessName: String,
    
    @ColumnInfo(name = "lat")
    val lat: Double,
    
    @ColumnInfo(name = "long")
    val long: Double,
    
    @ColumnInfo(name = "order_index")
    val orderIndex: Int,
    
    @ColumnInfo(name = "is_visited")
    val isVisited: Boolean = false,
    
    @ColumnInfo(name = "visited_at")
    val visitedAt: Long? = null // Added from the Phase 2 requirements
)