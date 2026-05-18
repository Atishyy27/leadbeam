package com.fieldflow.feature.business.di

import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.network.api.ApiService
import com.fieldflow.core.sync.SyncManager
import com.fieldflow.feature.business.data.repository.BusinessRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BusinessModule {
    
    @Provides
    @Singleton
    fun provideBusinessRepository(
        businessDao: BusinessDao,
        apiService: ApiService,
        syncManager: SyncManager
    ): BusinessRepository = BusinessRepository(
        businessDao = businessDao,
        apiService = apiService,
        syncManager = syncManager
    )
}