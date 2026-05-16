package com.fieldflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fieldflow.core.database.entity.BusinessEntity

@Dao
interface BusinessDao {
    @Query("""
        SELECT * FROM businesses 
        WHERE latGrid BETWEEN :minLatGrid AND :maxLatGrid 
        AND longGrid BETWEEN :minLongGrid AND :maxLongGrid
    """)
    suspend fun getBusinessesInGrid(
        minLatGrid: Int,
        maxLatGrid: Int,
        minLongGrid: Int,
        maxLongGrid: Int
    ): List<BusinessEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinesses(businesses: List<BusinessEntity>)

    @Query("DELETE FROM businesses")
    suspend fun clearBusinesses()
}