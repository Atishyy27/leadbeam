// feature/route/src/main/java/com/fieldflow/feature/route/di/RouteModule.kt
package com.fieldflow.feature.route.di

import com.fieldflow.core.database.dao.RouteDao
import com.fieldflow.feature.route.data.repository.RouteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RouteModule {
    
    @Provides
    @Singleton
    fun provideRouteRepository(
        routeDao: com.fieldflow.core.database.dao.RouteDao,
        businessDao: com.fieldflow.core.database.dao.BusinessDao,
        apiService: com.fieldflow.core.network.api.ApiService,
        syncManager: com.fieldflow.core.sync.SyncManager
    ): com.fieldflow.feature.route.data.repository.RouteRepository {
        return com.fieldflow.feature.route.data.repository.RouteRepository(
            routeDao = routeDao,
            businessDao = businessDao,
            apiService = apiService,
            syncManager = syncManager
        )
    }
}