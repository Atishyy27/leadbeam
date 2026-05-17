// core/database/src/main/java/com/fieldflow/core/database/entity/RouteEntities.kt
package com.fieldflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val id: String,
    val date: Long,
    val status: String // "active" or "completed"
)

@Entity(tableName = "route_stops")
data class RouteStopEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routeId: String,
    val businessId: String,
    val businessName: String, 
    val lat: Double,
    val long: Double,
    val orderIndex: Int,
    val isVisited: Boolean = false
)