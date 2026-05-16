package com.fieldflow.feature.map.data.repository

import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.entity.BusinessEntity
import com.fieldflow.core.network.api.ApiService
import com.fieldflow.core.network.model.NearbyResponse
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

    override fun getBusinessesInBounds(bounds: LatLngBounds): Flow<List<BusinessEntity>> = flow {
        val minLatGrid = floor(bounds.southwest.latitude / CELL_SIZE_DEGREES).toInt()
        val maxLatGrid = floor(bounds.northeast.latitude / CELL_SIZE_DEGREES).toInt()
        val minLongGrid = floor(bounds.southwest.longitude / CELL_SIZE_DEGREES).toInt()
        val maxLongGrid = floor(bounds.northeast.longitude / CELL_SIZE_DEGREES).toInt()

        // 1. Emit instantaneous offline cache
        val cached = businessDao.getBusinessesInGrid(minLatGrid, maxLatGrid, minLongGrid, maxLongGrid)
        emit(cached)

        // 2. Fetch fresh network data
        try {
            val response = apiService.getBusinessesNearby(
                startLat = bounds.southwest.latitude,
                startLong = bounds.southwest.longitude,
                endLat = bounds.northeast.latitude,
                endLong = bounds.northeast.longitude
            )

            val apiResponse = response.body() // ✅ Get the ApiResponse wrapper
            if (apiResponse?.status == 200 && apiResponse.data != null) {
                when (val data = apiResponse.data) {
                    is NearbyResponse.Individual -> {
                        val entities = data.businesses.map { dto ->
                            BusinessEntity(
                                leadbeamId = dto.leadbeamId,
                                name = dto.name,
                                lat = dto.lat,
                                long = dto.long,
                                latGrid = floor(dto.lat / CELL_SIZE_DEGREES).toInt(),
                                longGrid = floor(dto.long / CELL_SIZE_DEGREES).toInt(),
                                category = dto.categoryPrimary ?: "Unknown",
                                isChain = dto.isChain,
                                rating = dto.rating,
                                overallConfidence = dto.overallConfidence
                            )
                        }
                        businessDao.insertBusinesses(entities)
                        
                        val updatedCache = businessDao.getBusinessesInGrid(minLatGrid, maxLatGrid, minLongGrid, maxLongGrid)
                        emit(updatedCache)
                    }
                    is NearbyResponse.Clustered -> {
                        // Handled by UI layer
                    }
                    else -> { } 
                }
            }
        } catch (e: Exception) {
            // Fails silently
        }
    }
}