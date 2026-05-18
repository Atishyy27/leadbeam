// core/database/src/main/java/com/fieldflow/core/database/entity/RouteEntity.kt
package com.fieldflow.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "routes",
    indices = [
        Index("is_active"),
        Index("status")
    ]
)
data class RouteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String = "Today's Route", // Added default to prevent crashes with older code
    
    // NOTE: Changed back to String to support RouteListViewModel ("YYYY-MM-DD" parsing)
    @ColumnInfo(name = "date")
    val date: String, 
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "total_distance")
    val totalDistance: Double? = null,
    
    @ColumnInfo(name = "total_duration")
    val totalDuration: Int? = null,
    
    // NEW in v4
    @ColumnInfo(name = "status")
    val status: String = "active", // planned, active, paused, completed
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = false,
    
    @ColumnInfo(name = "started_at")
    val startedAt: Long? = null,
    
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null
)
