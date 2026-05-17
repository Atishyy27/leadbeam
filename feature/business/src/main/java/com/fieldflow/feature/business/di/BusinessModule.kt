// feature/business/src/main/java/com/fieldflow/feature/business/di/BusinessModule.kt
package com.fieldflow.feature.business.di

import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.network.ApiService
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
        apiService: ApiService,
        businessDao: BusinessDao
    ): BusinessRepository = BusinessRepository(apiService, businessDao)
}