package com.fieldflow.feature.map.domain.repository

import com.fieldflow.core.database.entity.BusinessEntity
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {
    fun getBusinessesInBounds(bounds: LatLngBounds): Flow<List<BusinessEntity>>
}