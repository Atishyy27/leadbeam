// core/common/src/main/java/com/fieldflow/core/common/di/ConnectivityModule.kt
package com.fieldflow.core.common.di

import com.fieldflow.core.common.connectivity.ConnectivityObserver
import com.fieldflow.core.common.connectivity.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {
    
    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        connectivityObserver: ConnectivityObserver
    ): NetworkMonitor
}