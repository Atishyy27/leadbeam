// core/network/src/main/java/com/fieldflow/core/network/dto/BusinessNearbyDto.kt
package com.fieldflow.core.network.dto

import com.google.gson.annotations.SerializedName

data class NearbyResponse(
    @SerializedName("businesses") val businesses: List<BusinessNearbyDto>,
    @SerializedName("total_count") val totalCount: Int? = null,
    @SerializedName("bounds") val bounds: Map<String, Double>? = null
)

data class BusinessNearbyDto(
    @SerializedName("leadbeam_id") val leadbeamId: String,
    @SerializedName("name") val name: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("long") val long: Double,
    @SerializedName("address_full") val addressFull: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("category_primary") val categoryPrimary: String,
    @SerializedName("category_group") val categoryGroup: String,
    @SerializedName("category_display") val categoryDisplay: String,
    @SerializedName("category_group_color") val categoryGroupColor: String,
    @SerializedName("is_chain") val isChain: Boolean,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("overall_confidence") val overallConfidence: Double
)