// core/sync/src/main/java/com/fieldflow/core/sync/workers/CacheCleanupWorker.kt
package com.fieldflow.core.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fieldflow.core.database.dao.BusinessDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class CacheCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val businessDao: BusinessDao
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Delete businesses not fetched in last 24 hours (and not favorited)
            val cutoffTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)
            businessDao.deleteStale(cutoffTime)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}