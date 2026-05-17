// core/database/src/main/java/com/fieldflow/core/database/entity/SyncQueueEntity.kt
package com.fieldflow.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sync_queue",
    indices = [Index("created_at")]
)
data class SyncQueueEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "entity_type")
    val entityType: String, // "route_stop", "business_favorite", etc.
    
    @ColumnInfo(name = "entity_id")
    val entityId: String,
    
    @ColumnInfo(name = "action")
    val action: String, // "mark_visited", "toggle_favorite", etc.
    
    @ColumnInfo(name = "data")
    val data: String? = null, // JSON payload
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    
    @ColumnInfo(name = "retry_count")
    val retryCount: Int = 0
)