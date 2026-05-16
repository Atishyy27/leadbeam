package com.fieldflow.core.network.interceptor

import com.fieldflow.core.datastore.PreferencesManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { preferencesManager.getAccessToken() }
        
        val request = chain.request().newBuilder().apply {
            if (!token.isNullHeader()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(request)
    }

    private fun String?.isNullHeader(): Boolean = this.isNullOrBlank()
}