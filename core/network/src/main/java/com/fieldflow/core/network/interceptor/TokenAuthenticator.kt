package com.fieldflow.core.network.interceptor

import com.fieldflow.core.database.PreferencesManager
import com.fieldflow.core.network.api.TokenRefreshService
import com.fieldflow.core.network.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val tokenRefreshService: TokenRefreshService
) : Authenticator {
    
    override fun authenticate(route: Route?, response: Response): Request? {
        // If we've already tried to refresh, give up
        if (response.request.header("Authorization-Retry") != null) {
            return null
        }

        return runBlocking {
            val refreshToken = preferencesManager.getRefreshToken() ?: return@runBlocking null

            try {
                // Call refresh API with proper data class
                val tokenResponse = tokenRefreshService.refreshToken(
                    RefreshTokenRequest(refreshToken)
                )

                // Check if response was successful
                if (tokenResponse.isSuccessful) {
                    val newTokens = tokenResponse.body()?.data
                    if (newTokens != null) {
                        // Save new tokens
                        preferencesManager.saveTokens(
                            newTokens.accessToken,
                            newTokens.refreshToken
                        )

                        // Retry request with new token
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${newTokens.accessToken}")
                            .header("Authorization-Retry", "true")
                            .build()
                    } else null
                } else {
                    // Refresh failed, clear tokens
                    preferencesManager.clearTokens()
                    null
                }
            } catch (e: Exception) {
                // Network error or parsing error
                preferencesManager.clearTokens()
                null
            }
        }
    }
}