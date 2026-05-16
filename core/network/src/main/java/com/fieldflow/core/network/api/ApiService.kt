package com.fieldflow.core.network.api

import com.fieldflow.core.network.model.ApiResponse
import com.fieldflow.core.network.model.LoginRequest
import com.fieldflow.core.network.model.TokenData
import com.fieldflow.core.network.model.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<TokenData>

    @GET("user/profile")
    suspend fun getUserProfile(): ApiResponse<UserProfile>

    @GET("businesses/nearby")
suspend fun getBusinessesNearby(
    @Query("start_lat") startLat: Double,
    @Query("start_long") startLong: Double,
    @Query("end_lat") endLat: Double,
    @Query("end_long") endLong: Double
): ApiResponse<com.fieldflow.core.network.model.NearbyResponse>
}