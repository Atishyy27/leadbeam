// core/database/src/main/java/com/fieldflow/core/database/dao/SyncQueueDao.kt
package com.fieldflow.core.database.dao

import androidx.room.*
import com.fieldflow.core.database.entity.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SyncQueueEntity)
    
    @Query("SELECT * FROM sync_queue ORDER BY created_at ASC")
    suspend fun getAllPending(): List<SyncQueueEntity>
    
    @Query("SELECT * FROM sync_queue ORDER BY created_at ASC")
    fun observePending(): Flow<List<SyncQueueEntity>>
    
    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun delete(id: String)
    
    @Query("UPDATE sync_queue SET retry_count = retry_count + 1 WHERE id = :id")
    suspend fun incrementRetry(id: String)
    
    @Query("DELETE FROM sync_queue WHERE retry_count > 5")
    suspend fun deleteFailedItems()
    
    @Transaction
    suspend fun deleteAll(ids: List<String>) {
        ids.forEach { delete(it) }
    }
}