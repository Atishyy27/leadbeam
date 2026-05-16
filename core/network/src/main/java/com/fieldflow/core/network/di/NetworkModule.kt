package com.fieldflow.core.network.di

import com.google.gson.GsonBuilder
import com.fieldflow.core.network.model.NearbyResponse
import com.fieldflow.core.network.model.NearbyResponseDeserializer

import com.fieldflow.core.network.api.ApiService
import com.fieldflow.core.network.api.TokenRefreshService
import com.fieldflow.core.network.interceptor.AuthInterceptor
import com.fieldflow.core.network.interceptor.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:3000/api/" // Standard Android Emulator Localhost loopback

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named("BaseClient")
    fun provideBaseOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("RefreshClient")
    fun provideRefreshOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(
        @Named("BaseClient") okHttpClient: OkHttpClient
    ): ApiService {
        val customGson = GsonBuilder()
            .registerTypeAdapter(NearbyResponse::class.java, NearbyResponseDeserializer())
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(customGson))
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenRefreshService(
        @Named("RefreshClient") okHttpClient: OkHttpClient
    ): TokenRefreshService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenRefreshService::class.java)
    }
}