package com.fieldflow.core.network.api

import com.fieldflow.core.network.model.ApiResponse
import com.fieldflow.core.network.model.RefreshTokenRequest
import com.fieldflow.core.network.model.TokenData
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenRefreshService {
    @POST("auth/token/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): ApiResponse<TokenData>
}