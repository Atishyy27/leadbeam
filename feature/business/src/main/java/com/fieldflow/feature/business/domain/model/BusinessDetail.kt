// feature/business/src/main/java/com/fieldflow/feature/business/domain/model/BusinessDetail.kt
package com.fieldflow.feature.business.domain.model

data class BusinessDetail(
    val leadbeamId: String,
    val name: String,
    val lat: Double,
    val long: Double,
    val addressFull: String,
    val city: String,
    val state: String,
    val postalCode: String?,
    val phonePrimary: String?,
    val email: String?,
    val website: String?,
    val categoryPrimary: String,
    val categoryGroup: String,
    val categoryDisplay: String,
    val categoryGroupDisplay: String,
    val categoryGroupColor: String,
    val isChain: Boolean,
    val chainName: String?,
    val operatingStatus: String,
    val operatingHours: String?,
    val hoursNotes: String?,
    val rating: Double?,
    val reviewsCount: Int?,
    val overallConfidence: Double,
    val dataCompleteness: Double,
    val sourceCount: Int,
    val socialProfiles: String?,
    val enrichmentData: String?,
    val description: String?,
    val parkingInfo: String?,
    val accessibility: String?,
    val paymentMethods: String?,
    val photos: String?,
    val lastVerified: String?,
    val isFavorite: Boolean,
    val isHidden: Boolean
) {
    val confidenceLevel: String
        get() = when {
            overallConfidence >= 0.9 -> "High"
            overallConfidence >= 0.7 -> "Medium"
            else -> "Low"
        }
    
    val isOpenNow: Boolean?
        get() {
            // Simplified - would parse operating_hours JSON in production
            return if (operatingStatus == "open") true else null
        }
}