package com.fieldflow.core.database.di
import com.fieldflow.core.database.dao.BusinessDao

import android.content.Context
import androidx.room.Room
import com.fieldflow.core.database.FieldFlowDatabase
import com.fieldflow.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFieldFlowDatabase(
        @ApplicationContext context: Context
    ): FieldFlowDatabase {
        return Room.databaseBuilder(
            context,
            FieldFlowDatabase::class.java,
            "fieldflow_cache.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: FieldFlowDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideBusinessDao(database: FieldFlowDatabase): BusinessDao {
        return database.businessDao()
    }
}