// core/sync/src/main/java/com/fieldflow/core/sync/workers/SyncWorker.kt
package com.fieldflow.core.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fieldflow.core.database.dao.RouteDao
import com.fieldflow.core.database.dao.SyncQueueDao
import com.fieldflow.core.network.api.ApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val routeDao: RouteDao
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val pendingItems = syncQueueDao.getAllPending()
            
            if (pendingItems.isEmpty()) {
                return@withContext Result.success()
            }
            
            val successfulIds = mutableListOf<String>()
            
            for (item in pendingItems) {
                try {
                    when (item.entityType) {
                        "route_stop" -> syncRouteStop(item)
                        "business_favorite" -> syncBusinessFavorite(item)
                        "business_hidden" -> syncBusinessHidden(item)
                        "route_completed" -> syncRouteCompleted(item)
                    }
                    successfulIds.add(item.id)
                } catch (e: Exception) {
                    // Increment retry count
                    syncQueueDao.incrementRetry(item.id)
                }
            }
            
            // Delete successful syncs
            if (successfulIds.isNotEmpty()) {
                syncQueueDao.deleteAll(successfulIds)
            }
            
            // Clean up failed items after too many retries
            syncQueueDao.deleteFailedItems()
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private suspend fun syncRouteStop(item: com.fieldflow.core.database.entity.SyncQueueEntity) {
        val data = JSONObject(item.data ?: "{}")
        val stopId = item.entityId
        val isVisited = data.getBoolean("isVisited")
        val visitedAt = data.optLong("visitedAt", 0L)
        
        // API call to sync visited status
        // This is a placeholder - actual endpoint depends on backend API
        // apiService.updateRouteStop(stopId, isVisited, visitedAt)
    }
    
    private suspend fun syncBusinessFavorite(item: com.fieldflow.core.database.entity.SyncQueueEntity) {
        val data = JSONObject(item.data ?: "{}")
        val businessId = item.entityId
        val isFavorite = data.getBoolean("isFavorite")
        
        // API call to sync favorite status
        // apiService.updateBusinessFavorite(businessId, isFavorite)
    }
    
    private suspend fun syncBusinessHidden(item: com.fieldflow.core.database.entity.SyncQueueEntity) {
        val data = JSONObject(item.data ?: "{}")
        val businessId = item.entityId
        val isHidden = data.getBoolean("isHidden")
        
        // API call to sync hidden status
        // apiService.updateBusinessHidden(businessId, isHidden)
    }
    
    private suspend fun syncRouteCompleted(item: com.fieldflow.core.database.entity.SyncQueueEntity) {
        val data = JSONObject(item.data ?: "{}")
        val routeId = item.entityId
        val completedAt = data.getLong("completedAt")
        
        // API call to sync route completion
        // apiService.completeRoute(routeId, completedAt)
    }
}