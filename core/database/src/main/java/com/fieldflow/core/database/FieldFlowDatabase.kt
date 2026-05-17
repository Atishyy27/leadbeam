// core/database/src/main/java/com/fieldflow/core/database/FieldFlowDatabase.kt
package com.fieldflow.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fieldflow.core.database.converters.Converters
import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.dao.CategoryDao
import com.fieldflow.core.database.dao.RouteDao
import com.fieldflow.core.database.dao.UserDao
import com.fieldflow.core.database.entities.BusinessEntity
import com.fieldflow.core.database.entities.CategoryEntity
import com.fieldflow.core.database.entities.UserEntity
import com.fieldflow.core.database.entity.RouteEntity
import com.fieldflow.core.database.entity.RouteStopEntity

@Database(
    entities = [
        UserEntity::class,
        BusinessEntity::class,
        CategoryEntity::class,
        RouteEntity::class,
        RouteStopEntity::class
    ],
    version = 4, // BUMPED to 4 for advanced route tracking fields (isActive, status)
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FieldFlowDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun businessDao(): BusinessDao
    abstract fun categoryDao(): CategoryDao
    abstract fun routeDao(): RouteDao
    
    companion object {
        const val DATABASE_NAME = "fieldflow_database"
    }
}