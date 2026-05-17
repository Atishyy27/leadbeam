package com.fieldflow.core.network.interceptor

import com.fieldflow.core.database.PreferencesManager // Change to .datastore if necessary
import com.fieldflow.core.network.api.TokenRefreshService
import com.fieldflow.core.network.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val tokenRefreshService: TokenRefreshService
) : Authenticator {
    
    // The lock that prevents multiple simultaneous refresh calls
    private val mutex = Mutex()
    
    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. Prevent infinite retry loops (From your old code - EXCELLENT)
        if (response.request.header("Authorization-Retry") != null) {
            return null
        }

        // 2. Safely get the new token using Mutex
        val newAccessToken = runBlocking { getNewTokenSafely() } ?: return null

        // 3. Retry the request with the new token AND the safety header
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .header("Authorization-Retry", "true")
            .build()
    }

    private suspend fun getNewTokenSafely(): String? {
        return try {
            // Lock the thread. If 5 requests fail at once, they wait in line here.
            mutex.withLock {
                val refreshToken = preferencesManager.getRefreshToken() ?: return null

                // Call refresh API
                val tokenResponse = tokenRefreshService.refreshToken(
                    RefreshTokenRequest(refreshToken)
                )

                if (tokenResponse.isSuccessful) {
                    val newTokens = tokenResponse.body()?.data
                    if (newTokens != null) {
                        // Save new tokens
                        preferencesManager.saveTokens(
                            newTokens.accessToken,
                            newTokens.refreshToken
                        )
                        return newTokens.accessToken
                    } else {
                        preferencesManager.clearTokens()
                        return null
                    }
                } else {
                    // Refresh failed (e.g., token expired), clear tokens to force re-login
                    preferencesManager.clearTokens()
                    return null
                }
            }
        } catch (e: Exception) {
            // Network error during refresh
            preferencesManager.clearTokens()
            return null
        }
    }
}