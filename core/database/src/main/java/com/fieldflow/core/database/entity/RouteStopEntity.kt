// core/database/src/main/java/com/fieldflow/core/database/entities/RouteStopEntity.kt
package com.fieldflow.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "route_stops",
    foreignKeys = [
        ForeignKey(
            entity = RouteEntity::class,
            parentColumns = ["id"],
            childColumns = ["route_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["leadbeam_id"],
            childColumns = ["business_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("route_id"),
        Index("business_id")
    ]
)
data class RouteStopEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "route_id")
    val routeId: String,
    
    @ColumnInfo(name = "business_id")
    val businessId: String,
    
    @ColumnInfo(name = "order_index")
    val orderIndex: Int,
    
    @ColumnInfo(name = "is_visited")
    val isVisited: Boolean = false,
    
    @ColumnInfo(name = "visited_at")
    val visitedAt: Long? = null,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null
)