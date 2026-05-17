// core/database/src/main/java/com/fieldflow/core/database/migrations/Migration_3_4.kt
package com.fieldflow.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add status and active tracking to routes table
        db.execSQL("ALTER TABLE routes ADD COLUMN status TEXT NOT NULL DEFAULT 'planned'")
        db.execSQL("ALTER TABLE routes ADD COLUMN is_active INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE routes ADD COLUMN started_at INTEGER")
        db.execSQL("ALTER TABLE routes ADD COLUMN completed_at INTEGER")
        
        // Create index for active route lookup
        db.execSQL("CREATE INDEX index_routes_is_active ON routes(is_active)")
        db.execSQL("CREATE INDEX index_routes_status ON routes(status)")
    }
}