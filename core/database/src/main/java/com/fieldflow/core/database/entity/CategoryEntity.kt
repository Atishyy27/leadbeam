// core/database/src/main/java/com/fieldflow/core/database/entity/CategoryEntity.kt
package com.fieldflow.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, 
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "display")
    val display: String,
    
    @ColumnInfo(name = "group_id")
    val groupId: String,
    
    @ColumnInfo(name = "group_name")
    val groupName: String,
    
    @ColumnInfo(name = "group_display")
    val groupDisplay: String,
    
    @ColumnInfo(name = "group_color")
    val groupColor: String,
    
    @ColumnInfo(name = "group_icon")
    val groupIcon: String
)