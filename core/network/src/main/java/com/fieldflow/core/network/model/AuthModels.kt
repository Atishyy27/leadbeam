package com.fieldflow.core.network.model

import com.google.gson.annotations.SerializedName

// The global response wrapper used by the backend
data class ApiResponse<T>(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class TokenData(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Long
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("company") val company: String,
    @SerializedName("title") val title: String,
    @SerializedName("territory") val territory: String
)