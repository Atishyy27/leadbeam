// feature/map/src/main/java/com/fieldflow/map/data/repository/BusinessRepositoryImpl.kt
package com.fieldflow.feature.map.data.repository

import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.entity.BusinessEntity
import com.fieldflow.core.network.api.ApiService
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.floor

@Singleton
class BusinessRepositoryImpl @Inject constructor(
    private val businessDao: BusinessDao,
    private val apiService: ApiService
) : com.fieldflow.feature.map.domain.repository.BusinessRepository {

    private val CELL_SIZE_DEGREES = 0.1

    override fun getBusinessesInBounds(bounds: LatLngBounds, categoryGroup: String?): Flow<List<BusinessEntity>> = flow {
        val minLatGrid = floor(bounds.southwest.latitude / CELL_SIZE_DEGREES).toInt()
        val maxLatGrid = floor(bounds.northeast.latitude / CELL_SIZE_DEGREES).toInt()
        val minLongGrid = floor(bounds.southwest.longitude / CELL_SIZE_DEGREES).toInt()
        val maxLongGrid = floor(bounds.northeast.longitude / CELL_SIZE_DEGREES).toInt()

        // 1. Emit cache immediately (offline-first)
        val cached = if (categoryGroup != null) {
            businessDao.getBusinessesInGridWithFilter(minLatGrid, maxLatGrid, minLongGrid, maxLongGrid, categoryGroup)
        } else {
            businessDao.getBusinessesInGrid(minLatGrid, maxLatGrid, minLongGrid, maxLongGrid)
        }
        emit(cached)

        // 2. Fetch network data
        try {
            val response = apiService.getBusinessesNearby(
                startLat = bounds.southwest.latitude,
                startLong = bounds.southwest.longitude,
                endLat = bounds.northeast.latitude,
                endLong = bounds.northeast.longitude
            )

            val nearbyData = response.body()?.data

            if (nearbyData != null) {
                println("FIELDFLOW_DEBUG: API returned ${nearbyData.businesses.size} items")

                val entities = nearbyData.businesses.map { dto ->
                    // FIX: Log exact categoryPrimary values to verify filter chip strings match
                    println("FIELDFLOW_DEBUG: category='${dto.categoryPrimary}' for ${dto.name}")

                    BusinessEntity(
                        leadbeamId = dto.leadbeamId,
                        name = dto.name,
                        lat = dto.lat,
                        long = dto.long,
                        latGrid = floor(dto.lat / CELL_SIZE_DEGREES).toInt(),
                        longGrid = floor(dto.long / CELL_SIZE_DEGREES).toInt(),
                        category = dto.categoryPrimary?.takeIf { it.isNotBlank() } ?: "",
                        categoryGroup = dto.categoryGroup?.takeIf { it.isNotBlank() } ?: "",
                        addressFull = dto.addressFull?.takeIf { it.isNotBlank() } ?: "",
                        city = dto.city?.takeIf { it.isNotBlank() } ?: "",
                        state = dto.state?.takeIf { it.isNotBlank() } ?: "",
                        isChain = dto.isChain,
                        rating = dto.rating,
                        overallConfidence = dto.overallConfidence
                    )
                }

                businessDao.insertBusinesses(entities)

                val updatedCache = businessDao.getBusinessesInGrid(
                    minLatGrid, maxLatGrid, minLongGrid, maxLongGrid
                )
                emit(updatedCache)
            }
        } catch (e: Exception) {
            // FIX: Log the actual error — "fails silently" hides real problems
            println("FIELDFLOW_DEBUG: Network fetch failed: ${e.message}")
            // Don't rethrow — offline cache was already emitted above
        }
    }
}