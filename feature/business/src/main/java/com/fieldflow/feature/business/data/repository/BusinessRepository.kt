// feature/business/src/main/java/com/fieldflow/feature/business/data/repository/BusinessRepository.kt
package com.fieldflow.feature.business.data.repository

import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.entities.BusinessEntity
import com.fieldflow.core.network.ApiService
import com.fieldflow.feature.business.data.mapper.toDomain
import com.fieldflow.feature.business.data.mapper.toEntity
import com.fieldflow.feature.business.domain.model.BusinessDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Result<Nothing>()
}

@Singleton
class BusinessRepository @Inject constructor(
    private val apiService: ApiService,
    private val businessDao: BusinessDao
) {
    
    fun getBusinessDetail(businessId: String): Flow<Result<BusinessDetail>> = flow {
        // 1. Emit cached immediately
        val cached = businessDao.getById(businessId)
        if (cached != null) {
            emit(Result.Success(cached.toDomain()))
        }
        
        // 2. Fetch fresh if needed
        val shouldRefresh = cached == null || shouldRefreshDetails(businessId)
        if (shouldRefresh) {
            try {
                val response = apiService.getBusinessDetail(businessId)
                val entity = response.data.toEntity(cached)
                businessDao.insert(entity)
                
                val fresh = businessDao.getById(businessId)
                if (fresh != null) {
                    emit(Result.Success(fresh.toDomain()))
                }
            } catch (e: Exception) {
                if (cached == null) {
                    emit(Result.Error("Failed to load business", e))
                }
            }
        }
    }
    
    fun getAllBusinesses(sortBy: String = "name", limit: Int = 50, offset: Int = 0): Flow<List<BusinessDetail>> {
        return businessDao.getAllBusinessesPaged(sortBy, limit, offset)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    fun getFavorites(): Flow<List<BusinessDetail>> {
        return businessDao.getFavorites()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    suspend fun toggleFavorite(businessId: String): Result<Unit> {
        return try {
            businessDao.toggleFavorite(businessId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to toggle favorite", e)
        }
    }
    
    suspend fun toggleHidden(businessId: String): Result<Unit> {
        return try {
            businessDao.toggleHidden(businessId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to toggle hidden", e)
        }
    }
    
    suspend fun getVisibleBusinessCount(): Int {
        return businessDao.getVisibleBusinessCount()
    }
    
    private suspend fun shouldRefreshDetails(businessId: String): Boolean {
        val cacheAge = businessDao.getDetailsCacheAge(businessId) ?: return true
        val age = System.currentTimeMillis() - cacheAge
        return age > 24 * 60 * 60 * 1000 // 24 hours
    }
}