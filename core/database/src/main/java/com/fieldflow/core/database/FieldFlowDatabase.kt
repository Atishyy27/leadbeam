package com.fieldflow.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.dao.UserDao
import com.fieldflow.core.database.entity.BusinessEntity
import com.fieldflow.core.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class, 
        BusinessEntity::class // Added mapping
    ],
    version = 2, // Bumped version for schema migration
    exportSchema = false
)
abstract class FieldFlowDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun businessDao(): BusinessDao
}