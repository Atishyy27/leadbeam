package com.fieldflow.core.network.interceptor

import com.fieldflow.core.database.PreferencesManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Skip auth for login and refresh endpoints
        if (request.url.encodedPath.contains("/auth/login") ||
            request.url.encodedPath.contains("/auth/token/refresh")
        ) {
            return chain.proceed(request)
        }

        // Add token to request
        val token = runBlocking { preferencesManager.getAccessToken() }
        
        val authenticatedRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(authenticatedRequest)
    }
}