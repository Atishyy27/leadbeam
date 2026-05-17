// feature/business/src/main/java/com/fieldflow/feature/business/data/repository/BusinessRepository.kt
package com.fieldflow.feature.business.data.repository

import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.entities.BusinessEntity
import com.fieldflow.core.network.ApiService
import com.fieldflow.core.sync.SyncManager
import com.fieldflow.feature.business.data.mapper.toDomain
import com.fieldflow.feature.business.data.mapper.toEntity
import com.fieldflow.feature.business.domain.model.BusinessDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Result<Nothing>()
}

@Singleton
class BusinessRepository @Inject constructor(
    private val businessDao: BusinessDao,
    private val apiService: ApiService,
    private val syncManager: SyncManager
) {
    
    // OFFLINE-FIRST: Emit cached immediately, fetch fresh in background
    fun getBusinessesInBounds(
        minLat: Double,
        maxLat: Double,
        minLong: Double,
        maxLong: Double,
        forceRefresh: Boolean = false
    ): Flow<List<BusinessDetail>> = flow {
        var hasFetched = false
        
        businessDao.getBusinessesInBounds(minLat, maxLat, minLong, maxLong).collect { cached ->
            // 1. Always emit cached data first
            emit(cached.map { it.toDomain() })
            
            // 2. Fetch fresh if cache stale or forced refresh (Run only once per flow collection)
            if (!hasFetched) {
                hasFetched = true
                val cacheAge = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
                val cachedCount = businessDao.getCachedCountInBounds(
                    minLat, maxLat, minLong, maxLong, cacheAge
                )
                
                if (forceRefresh || cachedCount == 0) {
                    try {
                        val response = apiService.getBusinessesNearby(
                            startLat = minLat,
                            startLong = minLong,
                            endLat = maxLat,
                            endLong = maxLong
                        )
                        
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                val timestamp = System.currentTimeMillis()
                                val entities = body.data.businesses.map { dto ->
                                    dto.toEntity().copy(lastFetched = timestamp)
                                }
                                businessDao.insertAll(entities)
                                // Room auto-emits updated data through Flow
                            }
                        }
                    } catch (e: Exception) {
                        // Cached data already emitted, silent failure for background sync
                    }
                }
            }
        }
    }
    
    // OFFLINE-FIRST: Detail screen
    fun getBusinessDetail(businessId: String): Flow<Result<BusinessDetail>> = flow {
        var hasFetched = false
        
        businessDao.getByIdFlow(businessId).collect { cached ->
            // 1. Emit cached immediately if available
            if (cached != null) {
                emit(Result.Success(cached.toDomain()))
            }
            
            // 2. Fetch fresh if cache older than 24 hours
            if (!hasFetched) {
                hasFetched = true
                val shouldFetch = cached == null || 
                    (cached.detailsCachedAt ?: 0) < System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)
                
                if (shouldFetch) {
                    try {
                        val response = apiService.getBusinessDetail(businessId)
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                // Fallback to raw entity if cached is null to satisfy mapper
                                val entity = body.data.toEntity(cached).copy(
                                    detailsCachedAt = System.currentTimeMillis(),
                                    lastFetched = System.currentTimeMillis()
                                )
                                businessDao.insert(entity)
                                // Room auto-emits updated data
                            } else if (cached == null) {
                                emit(Result.Error("Response body was empty"))
                            }
                        } else if (cached == null) {
                            emit(Result.Error("Failed to load details: ${response.code()} - ${response.message()}"))
                        }
                    } catch (e: Exception) {
                        if (cached == null) {
                            emit(Result.Error("Failed to load business details", e))
                        }
                    }
                }
            }
        }
    }
    
    // OPTIMISTIC UPDATE: Toggle favorite
    suspend fun toggleFavorite(businessId: String): Result<Unit> {
        return try {
            // 1. Update local DB immediately
            businessDao.toggleFavorite(businessId)
            
            // 2. Queue sync
            val business = businessDao.getById(businessId)
            val data = JSONObject().apply {
                put("isFavorite", business?.isFavorite ?: false)
            }.toString()
            
            syncManager.enqueueSyncItem(
                entityType = "business_favorite",
                entityId = businessId,
                action = "toggle",
                data = data
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to toggle favorite", e)
        }
    }
    
    // OPTIMISTIC UPDATE: Toggle hidden
    suspend fun toggleHidden(businessId: String): Result<Unit> {
        return try {
            // 1. Update local DB immediately
            businessDao.toggleHidden(businessId)
            
            // 2. Queue sync
            val business = businessDao.getById(businessId)
            val data = JSONObject().apply {
                put("isHidden", business?.isHidden ?: false)
            }.toString()
            
            syncManager.enqueueSyncItem(
                entityType = "business_hidden",
                entityId = businessId,
                action = "toggle",
                data = data
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to toggle hidden", e)
        }
    }
    
    fun getFavorites(): Flow<List<BusinessDetail>> {
        return businessDao.getFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getAllBusinesses(sortBy: String = "name", limit: Int = 50, offset: Int = 0): Flow<List<BusinessDetail>> {
        return businessDao.getAllBusinessesPaged(sortBy, limit, offset)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    suspend fun getVisibleBusinessCount(): Int {
        return businessDao.getVisibleBusinessCount()
    }
}