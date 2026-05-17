// core/database/src/main/java/com/fieldflow/core/database/di/DatabaseModule.kt
package com.fieldflow.core.database.di

import android.content.Context
import androidx.room.Room
import com.fieldflow.core.database.FieldFlowDatabase
import com.fieldflow.core.database.dao.BusinessDao
import com.fieldflow.core.database.dao.CategoryDao
import com.fieldflow.core.database.dao.RouteDao
import com.fieldflow.core.database.dao.UserDao

// NOTE: Uncomment these imports only if you actually created the physical migration files!
// import com.fieldflow.core.database.migrations.MIGRATION_1_2
// import com.fieldflow.core.database.migrations.MIGRATION_2_3
// import com.fieldflow.core.database.migrations.MIGRATION_3_4

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
            FieldFlowDatabase.DATABASE_NAME
        )
        // .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4) 
        .fallbackToDestructiveMigration() // Continues to be your best friend during rapid prototyping
        .build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: FieldFlowDatabase): UserDao = database.userDao()
    
    @Provides
    @Singleton
    fun provideBusinessDao(database: FieldFlowDatabase): BusinessDao = database.businessDao()
    
    @Provides
    @Singleton
    fun provideCategoryDao(database: FieldFlowDatabase): CategoryDao = database.categoryDao()
    
    @Provides
    @Singleton
    fun provideRouteDao(database: FieldFlowDatabase): RouteDao = database.routeDao()
}