// core/database/src/main/java/com/fieldflow/core/database/entity/BusinessEntity.kt
package com.fieldflow.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "businesses",
    indices = [
        Index(value = ["lat_grid", "long_grid"]),
        Index(value = ["category"]),
        Index(value = ["is_favorite"]),
        Index(value = ["is_hidden"]),
        Index(value = ["last_fetched"])
    ]
)
data class BusinessEntity(
    @PrimaryKey 
    @ColumnInfo(name = "leadbeam_id") 
    val leadbeamId: String,
    
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "long") val long: Double,
    
    // Grid Mapping (From V1)
    @ColumnInfo(name = "lat_grid") val latGrid: Int = 0,
    @ColumnInfo(name = "long_grid") val longGrid: Int = 0,
    
    // Core details
    @ColumnInfo(name = "category") val category: String, 
    @ColumnInfo(name = "is_chain") val isChain: Boolean = false,
    @ColumnInfo(name = "rating") val rating: Double? = null,
    
    // Confidence fields (Combined from V1 & V2 to prevent mapper crashes)
    @ColumnInfo(name = "overall_confidence") val overallConfidence: Double = 1.0,
    @ColumnInfo(name = "confidence") val confidence: Double = 1.0,

    // Address & Contact (Combined)
    @ColumnInfo(name = "address_full") val addressFull: String = "",
    @ColumnInfo(name = "city") val city: String = "",
    @ColumnInfo(name = "state") val state: String = "",
    @ColumnInfo(name = "address_locality") val addressLocality: String? = null,
    @ColumnInfo(name = "address_region") val addressRegion: String? = null,
    @ColumnInfo(name = "postal_code") val postalCode: String? = null,
    @ColumnInfo(name = "phone_primary") val phonePrimary: String? = null,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "website") val website: String? = null,

    // Category Hierarchy
    @ColumnInfo(name = "category_group") val categoryGroup: String = "",
    @ColumnInfo(name = "category_display") val categoryDisplay: String = "",
    @ColumnInfo(name = "category_group_display") val categoryGroupDisplay: String = "",
    @ColumnInfo(name = "category_group_color") val categoryGroupColor: String = "",
    @ColumnInfo(name = "chain_name") val chainName: String? = null,
    
    // Operations & Status
    @ColumnInfo(name = "operating_status") val operatingStatus: String = "OPERATIONAL",
    @ColumnInfo(name = "operating_hours") val operatingHours: String? = null,
    @ColumnInfo(name = "reviews_count") val reviewsCount: Int? = null,
    
    // Enrichment & Cache Metadata
    @ColumnInfo(name = "data_completeness") val dataCompleteness: Double = 1.0,
    @ColumnInfo(name = "source_count") val sourceCount: Int = 1,
    @ColumnInfo(name = "social_profiles") val socialProfiles: String? = null,
    @ColumnInfo(name = "enrichment_data") val enrichmentData: String? = null,
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_fetched") val lastFetched: Long = 0, // From V5 Cache tracking
    
    // User Interactions (V2)
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "is_hidden") val isHidden: Boolean = false,
    
    // Deep Details (Fetched from /businesses/:id)
    @ColumnInfo(name = "details_cached_at") val detailsCachedAt: Long? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "hours_notes") val hoursNotes: String? = null,
    @ColumnInfo(name = "parking_info") val parkingInfo: String? = null,
    @ColumnInfo(name = "accessibility") val accessibility: String? = null,
    @ColumnInfo(name = "payment_methods") val paymentMethods: String? = null,
    @ColumnInfo(name = "photos") val photos: String? = null,
    @ColumnInfo(name = "last_verified") val lastVerified: String? = null 
)