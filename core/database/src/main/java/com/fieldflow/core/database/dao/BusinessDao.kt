// core/database/src/main/java/com/fieldflow/core/database/dao/BusinessDao.kt
package com.fieldflow.core.database.dao

import androidx.room.*
import com.fieldflow.core.database.entity.BusinessEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {
    
    // --- INSERT & UPDATE ---
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(business: BusinessEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(businesses: List<BusinessEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinesses(businesses: List<BusinessEntity>) // Kept for compatibility
    
    @Update
    suspend fun update(business: BusinessEntity)
    
    // --- QUERIES ---

    @Query("SELECT * FROM businesses WHERE leadbeam_id = :id")
    suspend fun getById(id: String): BusinessEntity?
    
    @Query("SELECT * FROM businesses WHERE leadbeam_id = :id")
    fun getByIdFlow(id: String): Flow<BusinessEntity?>

    @Query("SELECT * FROM businesses")
    fun getAll(): List<BusinessEntity>
    
    @Query("""
        SELECT * FROM businesses 
        WHERE is_hidden = 0
        AND lat BETWEEN :minLat AND :maxLat 
        AND long BETWEEN :minLong AND :maxLong
        ORDER BY 
            CASE 
                WHEN is_favorite = 1 THEN 0 
                ELSE 1 
            END,
            confidence DESC,
            overall_confidence DESC
    """)
    fun getBusinessesInBounds(
        minLat: Double,
        maxLat: Double,
        minLong: Double,
        maxLong: Double
    ): Flow<List<BusinessEntity>>

    @Query("""
        SELECT * FROM businesses 
        WHERE lat_grid BETWEEN :minLatGrid AND :maxLatGrid 
        AND long_grid BETWEEN :minLongGrid AND :maxLongGrid
    """)
    suspend fun getBusinessesInGrid(
        minLatGrid: Int,
        maxLatGrid: Int,
        minLongGrid: Int,
        maxLongGrid: Int
    ): List<BusinessEntity>
    
    // --- FAVORITES & HIDDEN ---
    
    @Query("SELECT * FROM businesses WHERE is_favorite = 1 AND is_hidden = 0 ORDER BY name ASC")
    fun getFavorites(): Flow<List<BusinessEntity>>
    
    @Query("SELECT * FROM businesses WHERE is_hidden = 1 ORDER BY name ASC")
    fun getHidden(): Flow<List<BusinessEntity>>
    
    // Direct updates are faster and avoid read-then-write race conditions
    @Query("UPDATE businesses SET is_favorite = NOT is_favorite WHERE leadbeam_id = :id")
    suspend fun toggleFavorite(id: String)
    
    @Query("UPDATE businesses SET is_hidden = NOT is_hidden WHERE leadbeam_id = :id")
    suspend fun toggleHidden(id: String)
    
    // --- LIST & PAGINATION ---
    
    @Query("""
        SELECT * FROM businesses 
        WHERE is_hidden = 0 
        ORDER BY 
            CASE WHEN :sortBy = 'name' THEN name END ASC,
            CASE WHEN :sortBy = 'rating' THEN rating END DESC,
            CASE WHEN :sortBy = 'confidence' THEN overall_confidence END DESC
        LIMIT :limit OFFSET :offset
    """)
    fun getAllBusinessesPaged(
        sortBy: String = "name",
        limit: Int = 50,
        offset: Int = 0
    ): Flow<List<BusinessEntity>>
    
    @Query("SELECT COUNT(*) FROM businesses WHERE is_hidden = 0")
    suspend fun getVisibleBusinessCount(): Int
    
    // --- CACHE & CLEANUP ---
    
    @Query("SELECT details_cached_at FROM businesses WHERE leadbeam_id = :businessId")
    suspend fun getDetailsCacheAge(businessId: String): Long?
    
    @Query("DELETE FROM businesses WHERE cached_at < :threshold")
    suspend fun deleteOlderThan(threshold: Long)

    @Query("DELETE FROM businesses WHERE last_fetched < :timestamp AND is_favorite = 0")
    suspend fun deleteStale(timestamp: Long)
    
    @Query("UPDATE businesses SET last_fetched = :timestamp WHERE leadbeam_id IN (:ids)")
    suspend fun updateFetchTime(ids: List<String>, timestamp: Long)
    
    @Query("""
        SELECT COUNT(*) FROM businesses 
        WHERE last_fetched > :timestamp 
        AND lat BETWEEN :minLat AND :maxLat 
        AND long BETWEEN :minLong AND :maxLong
    """)
    suspend fun getCachedCountInBounds(
        minLat: Double,
        maxLat: Double,
        minLong: Double,
        maxLong: Double,
        timestamp: Long
    ): Int
    
    @Query("DELETE FROM businesses")
    suspend fun deleteAll()

    @Query("DELETE FROM businesses")
    suspend fun clearBusinesses() // Kept for compatibility
}