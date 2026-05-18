// core/sync/src/main/java/com/fieldflow/core/sync/SyncManager.kt
package com.fieldflow.core.sync

import androidx.work.*
import com.fieldflow.core.database.dao.SyncQueueDao
import com.fieldflow.core.database.entity.SyncQueueEntity
import com.fieldflow.core.sync.workers.SyncWorker
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val workManager: WorkManager,
    private val syncQueueDao: SyncQueueDao
) {
    
    @OptIn(DelicateCoroutinesApi::class)
    fun enqueueSyncItem(
        entityType: String,
        entityId: String,
        action: String,
        data: String? = null
    ) {
        GlobalScope.launch {
            val item = SyncQueueEntity(
                id = UUID.randomUUID().toString(),
                entityType = entityType,
                entityId = entityId,
                action = action,
                data = data,
                createdAt = System.currentTimeMillis()
            )
            syncQueueDao.insert(item)
            scheduleSync()
        }
    }
    
    fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                1,
                TimeUnit.MINUTES
            )
            .addTag(SYNC_WORK_TAG)
            .build()
        
        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
    
    fun observePendingSync(): Flow<List<SyncQueueEntity>> {
        return syncQueueDao.observePending()
    }
    
    companion object {
        private const val SYNC_WORK_NAME = "fieldflow_sync"
        private const val SYNC_WORK_TAG = "sync"
    }
}