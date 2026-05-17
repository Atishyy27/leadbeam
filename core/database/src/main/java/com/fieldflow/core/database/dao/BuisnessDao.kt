package com.fieldflow.core.database.dao

import androidx.room.*
import com.fieldflow.core.database.entities.BusinessEntity
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

    @Query("SELECT * FROM businesses WHERE leadbeam_id = :businessId")
    suspend fun getById(businessId: String): BusinessEntity?
    
    @Query("SELECT * FROM businesses WHERE leadbeam_id = :businessId")
    fun getByIdFlow(businessId: String): Flow<BusinessEntity?>
    
    @Query("""
        SELECT * FROM businesses 
        WHERE lat BETWEEN :southwestLat AND :northeastLat
        AND long BETWEEN :southwestLong AND :northeastLong
        AND is_hidden = 0
        ORDER BY overall_confidence DESC
    """)
    fun getBusinessesInBounds(
        southwestLat: Double,
        southwestLong: Double,
        northeastLat: Double,
        northeastLong: Double
    ): Flow<List<BusinessEntity>>

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
    
    // --- FAVORITES & HIDDEN ---
    
    @Query("SELECT * FROM businesses WHERE is_favorite = 1 AND is_hidden = 0 ORDER BY name ASC")
    fun getFavorites(): Flow<List<BusinessEntity>>
    
    @Transaction
    suspend fun toggleFavorite(businessId: String) {
        val business = getById(businessId) ?: return
        update(business.copy(isFavorite = !business.isFavorite))
    }
    
    @Transaction
    suspend fun toggleHidden(businessId: String) {
        val business = getById(businessId) ?: return
        update(business.copy(isHidden = !business.isHidden))
    }
    
    @Query("SELECT * FROM businesses WHERE is_hidden = 1 ORDER BY name ASC")
    fun getHidden(): Flow<List<BusinessEntity>>
    
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
    
    @Query("DELETE FROM businesses")
    suspend fun deleteAll()

    @Query("DELETE FROM businesses")
    suspend fun clearBusinesses() // Kept for compatibility
}