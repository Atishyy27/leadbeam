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
        routeDao: RouteDao
    ): RouteRepository = RouteRepository(routeDao)
}