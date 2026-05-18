// core/network/src/main/java/com/fieldflow/core/network/api/ApiService.kt
package com.fieldflow.core.network.api

import com.fieldflow.core.network.dto.BusinessDetailResponse
import com.fieldflow.core.network.dto.CategoriesResponse
import com.fieldflow.core.network.dto.RouteOptimizationResponse
import com.fieldflow.core.network.model.ApiResponse
import com.fieldflow.core.network.model.LoginRequest
import com.fieldflow.core.network.model.RefreshTokenRequest
import com.fieldflow.core.network.model.TokenData
import com.fieldflow.core.network.model.UserProfile
import com.fieldflow.core.network.dto.NearbyResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // --- AUTHENTICATION ---
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<TokenData>>
    
    // Token Refresh
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
    
    @GET("businesses/{leadbeam_id}")
    suspend fun getBusinessDetail(
        @Path("leadbeam_id") leadbeamId: String
    ): Response<ApiResponse<BusinessDetailResponse>>
    
    // --- CATEGORIES ---
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<CategoriesResponse>>

    // --- ROUTE OPTIMIZATION ---
    @PATCH("routes/{route_id}/optimize")
    suspend fun optimizeRoute(
        @Path("route_id") routeId: String
    ): Response<RouteOptimizationResponse>
}