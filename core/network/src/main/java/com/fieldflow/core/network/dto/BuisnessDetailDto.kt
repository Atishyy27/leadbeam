// core/network/src/main/java/com/fieldflow/core/network/dto/BusinessDetailDto.kt
package com.fieldflow.core.network.dto

import com.google.gson.annotations.SerializedName

data class BusinessDetailResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: BusinessDetailDto
)

data class BusinessDetailDto(
    @SerializedName("leadbeam_id") val leadbeamId: String,
    @SerializedName("name") val name: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("long") val long: Double,
    @SerializedName("address_full") val addressFull: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("postal_code") val postalCode: String?,
    @SerializedName("phone_primary") val phonePrimary: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("website") val website: String?,
    @SerializedName("category_primary") val categoryPrimary: String,
    @SerializedName("category_group") val categoryGroup: String,
    @SerializedName("category_display") val categoryDisplay: String,
    @SerializedName("category_group_display") val categoryGroupDisplay: String,
    @SerializedName("category_group_color") val categoryGroupColor: String,
    @SerializedName("is_chain") val isChain: Boolean,
    @SerializedName("chain_name") val chainName: String?,
    @SerializedName("operating_status") val operatingStatus: String,
    @SerializedName("operating_hours") val operatingHours: Map<String, String>?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("reviews_count") val reviewsCount: Int?,
    @SerializedName("overall_confidence") val overallConfidence: Double,
    @SerializedName("data_completeness") val dataCompleteness: Double,
    @SerializedName("source_count") val sourceCount: Int,
    @SerializedName("social_profiles") val socialProfiles: Map<String, String>?,
    @SerializedName("enrichment_data") val enrichmentData: Map<String, String>?,
    @SerializedName("description") val description: String?,
    @SerializedName("hours_notes") val hoursNotes: String?,
    @SerializedName("parking_info") val parkingInfo: String?,
    @SerializedName("accessibility") val accessibility: String?,
    @SerializedName("payment_methods") val paymentMethods: List<String>?,
    @SerializedName("photos") val photos: List<String>?,
    @SerializedName("last_verified") val lastVerified: String?
)