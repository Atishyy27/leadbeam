package com.fieldflow.core.network.model

import com.google.gson.annotations.SerializedName

data class BusinessDto(
    @SerializedName("leadbeam_id") val leadbeamId: String,
    val name: String,
    val lat: Double,
    val long: Double,
    @SerializedName("category_primary") val categoryPrimary: String,
    @SerializedName("is_chain") val isChain: Boolean,
    val rating: Double?,
    @SerializedName("overall_confidence") val overallConfidence: Double
)

data class ClusterDto(
    @SerializedName("center_lat") val centerLat: Double,
    @SerializedName("center_long") val centerLong: Double,
    val count: Int
)

sealed class NearbyResponse {
    data class Individual(val businesses: List<BusinessDto>) : NearbyResponse()
    data class Clustered(val clusters: List<ClusterDto>) : NearbyResponse()
}