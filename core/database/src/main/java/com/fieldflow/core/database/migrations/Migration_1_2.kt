// core/database/src/main/java/com/fieldflow/core/database/migrations/Migration_1_2.kt
package com.fieldflow.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // User interaction columns
        db.execSQL("ALTER TABLE businesses ADD COLUMN is_favorite INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE businesses ADD COLUMN is_hidden INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE businesses ADD COLUMN details_cached_at INTEGER")
        
        // Detail fields
        db.execSQL("ALTER TABLE businesses ADD COLUMN description TEXT")
        db.execSQL("ALTER TABLE businesses ADD COLUMN hours_notes TEXT")
        db.execSQL("ALTER TABLE businesses ADD COLUMN parking_info TEXT")
        db.execSQL("ALTER TABLE businesses ADD COLUMN accessibility TEXT")
        db.execSQL("ALTER TABLE businesses ADD COLUMN payment_methods TEXT")
        db.execSQL("ALTER TABLE businesses ADD COLUMN photos TEXT")
        db.execSQL("ALTER TABLE businesses ADD COLUMN last_verified TEXT")
        
        // Indexes for performance
        db.execSQL("CREATE INDEX index_businesses_is_favorite ON businesses(is_favorite)")
        db.execSQL("CREATE INDEX index_businesses_is_hidden ON businesses(is_hidden)")
    }
}