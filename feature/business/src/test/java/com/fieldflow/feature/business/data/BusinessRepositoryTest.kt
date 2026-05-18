// feature/business/src/test/java/com/fieldflow/feature/business/data/BusinessRepositoryTest.kt
package com.fieldflow.feature.business.data

import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.entity.BusinessEntity
import com.fieldflow.core.network.api.ApiService
import com.fieldflow.core.sync.SyncManager
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class BusinessRepositoryTest {
    
    private lateinit var repository: BusinessRepository
    private lateinit var dao: BusinessDao
    private lateinit var apiService: ApiService
    private lateinit var syncManager: SyncManager
    
    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        apiService = mockk()
        syncManager = mockk(relaxed = true)
        repository = BusinessRepository(dao, apiService, syncManager)
    }
    
    @Test
    fun `getBusinessesInBounds emits cached data first`() = runTest {
        // Given
        val cachedBusiness = BusinessEntity(
            leadbeamId = "1",
            name = "Test Business",
            category = "restaurant",
            lat = 30.0,
            long = -97.0,
            addressFull = "123 Main St",
            addressLocality = "Austin",
            addressRegion = "TX",
            confidence = 0.9,
            phone = null,
            rating = null,
            lastFetched = System.currentTimeMillis()
        )
        
        every { dao.getBusinessesInBounds(any(), any(), any(), any()) } returns 
            flowOf(listOf(cachedBusiness))
        coEvery { dao.getCachedCountInBounds(any(), any(), any(), any(), any()) } returns 1
        
        // When
        val result = repository.getBusinessesInBounds(29.0, 31.0, -98.0, -96.0).first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Test Business", result[0].name)
    }
    
    @Test
    fun `toggleFavorite queues sync`() = runTest {
        // Given
        val businessId = "business_123"
        val business = BusinessEntity(
            leadbeamId = businessId,
            name = "Test",
            category = "restaurant",
            lat = 30.0,
            long = -97.0,
            addressFull = "123 Main",
            addressLocality = "Austin",
            addressRegion = "TX",
            confidence = 0.9,
            phone = null,
            rating = null,
            isFavorite = true,
            lastFetched = System.currentTimeMillis()
        )
        
        coEvery { dao.getById(businessId) } returns business
        
        // When
        repository.toggleFavorite(businessId)
        
        // Then
        coVerify { dao.toggleFavorite(businessId) }
        verify { syncManager.enqueueSyncItem("business_favorite", businessId, "toggle", any()) }
    }
}