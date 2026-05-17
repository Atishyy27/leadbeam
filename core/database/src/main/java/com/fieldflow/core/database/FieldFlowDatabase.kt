package com.fieldflow.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.dao.UserDao
import com.fieldflow.core.database.entity.BusinessEntity
import com.fieldflow.core.database.entity.UserEntity

import com.fieldflow.core.database.dao.RouteDao
// import com.fieldflow.core.database.entity.CategoryEntity // (If you have this)
import com.fieldflow.core.database.entity.RouteEntity
import com.fieldflow.core.database.entity.RouteStopEntity

@Database(
    entities = [
        UserEntity::class, 
        BusinessEntity::class, 
        // CategoryEntity::class,
        RouteEntity::class,       // ADD THIS
        RouteStopEntity::class    // ADD THIS
    ],
    version = 1,
    exportSchema = false
)
abstract class FieldFlowDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun businessDao(): BusinessDao
    abstract fun routeDao(): RouteDao // ADD THIS
}