package com.fieldflow.core.network.interceptor

import com.fieldflow.core.datastore.PreferencesManager
import com.fieldflow.core.network.api.TokenRefreshService
import com.fieldflow.core.network.model.RefreshTokenRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.runBlocking
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

    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Guard rail: Only intercept actual 401 Unauthorized errors
        if (response.code != 401) return null

        return runBlocking {
            refreshMutex.withLock {
                val currentLocalToken = preferencesManager.getAccessToken()
                val requestHeaderToken = response.request.header("Authorization")
                    ?.removePrefix("Bearer ")

                // Race Condition Check: If the token in the failing request doesn't match
                // our local token, another concurrent thread already completed the refresh.
                if (requestHeaderToken != currentLocalToken) {
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $currentLocalToken")
                        .build()
                }

                // If they match, we are the first thread to hit the wall. Execute refresh.
                val currentRefreshToken = preferencesManager.getRefreshToken()
                if (currentRefreshToken.isNullOrBlank()) {
                    preferencesManager.clearTokens()
                    return@withLock null
                }

                try {
                    val refreshResponse = tokenRefreshService.refreshToken(
                        RefreshTokenRequest(currentRefreshToken)
                    )

                    val newTokens = refreshResponse.data
                    if (refreshResponse.status == 200 && newTokens != null) {
                        preferencesManager.saveTokens(
                            accessToken = newTokens.accessToken,
                            refreshToken = newTokens.refreshToken
                        )

                        // Retry the failed request with the fresh token
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${newTokens.accessToken}")
                            .build()
                    } else {
                        // Refresh token was rejected or expired on server side
                        preferencesManager.clearTokens()
                        null
                    }
                } catch (e: Exception) {
                    // Network crash during refresh handshake
                    null
                }
            }
        }
    }
}