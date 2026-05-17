// core/network/src/main/java/com/fieldflow/core/network/api/ApiService.kt
package com.fieldflow.core.network.api

import com.fieldflow.core.network.model.* // Change to .dto.* if your folder is named dto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // --- AUTHENTICATION ---
    
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<TokenData>>
    
    // NEW: Token Refresh
    @POST("auth/token/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<ApiResponse<TokenData>> // Adjust type to RefreshTokenResponse if you created a separate one

    // --- USER ---
    
    @GET("user/profile")
    suspend fun getUserProfile(): Response<ApiResponse<UserProfile>>

    // --- BUSINESSES ---
    
    @GET("businesses/nearby")
    suspend fun getBusinessesNearby(
        @Query("start_lat") startLat: Double,
        @Query("start_long") startLong: Double,
        @Query("end_lat") endLat: Double,
        @Query("end_long") endLong: Double,
        @Query("category_ids") categoryIds: String? = null // NEW from v2
    ): Response<ApiResponse<NearbyResponse>>
    
    // NEW: Business Details
    @GET("businesses/{leadbeam_id}")
    suspend fun getBusinessDetail(
        @Path("leadbeam_id") leadbeamId: String
    ): Response<ApiResponse<BusinessDetailResponse>>
    
    // --- CATEGORIES ---
    
    // NEW: Categories
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<CategoriesResponse>>
}