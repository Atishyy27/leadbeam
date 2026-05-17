// core/database/src/main/java/com/fieldflow/core/database/di/DatabaseModule.kt
package com.fieldflow.core.database.di

import android.content.Context
import androidx.room.Room
import com.fieldflow.core.database.FieldFlowDatabase
import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.dao.CategoryDao
import com.fieldflow.core.database.dao.RouteDao
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
            FieldFlowDatabase.DATABASE_NAME // Uses the constant we just added to the DB file
        )
        .fallbackToDestructiveMigration() // Will save your life during rapid prototyping
        .build()
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

    @Provides
    @Singleton
    fun provideCategoryDao(database: FieldFlowDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideRouteDao(database: FieldFlowDatabase): RouteDao {
        return database.routeDao()
    }
}