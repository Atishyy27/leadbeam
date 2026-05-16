package com.fieldflow.feature.map.domain.usecase

import com.fieldflow.feature.map.domain.model.MapBusinessItem
import com.fieldflow.feature.map.domain.repository.BusinessRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBusinessesInBoundsUseCase @Inject constructor(
    private val repository: BusinessRepository
) {
    operator fun invoke(bounds: LatLngBounds): Flow<List<MapBusinessItem>> {
        return repository.getBusinessesInBounds(bounds).map { entities ->
            entities
                .filter { it.overallConfidence > 0.7 } // Drop low-quality data
                .map { entity ->
                    MapBusinessItem(
                        id = entity.leadbeamId,
                        businessName = entity.name,
                        category = entity.category,
                        location = LatLng(entity.lat, entity.long)
                    )
                }
        }
    }
}