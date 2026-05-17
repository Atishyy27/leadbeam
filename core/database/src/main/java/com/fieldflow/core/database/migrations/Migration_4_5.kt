// core/database/src/main/java/com/fieldflow/core/database/migrations/Migration_4_5.kt
package com.fieldflow.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add last_fetched to businesses for cache TTL
        db.execSQL("ALTER TABLE businesses ADD COLUMN last_fetched INTEGER NOT NULL DEFAULT 0")
        
        // Create sync queue table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS sync_queue (
                id TEXT PRIMARY KEY NOT NULL,
                entity_type TEXT NOT NULL,
                entity_id TEXT NOT NULL,
                action TEXT NOT NULL,
                data TEXT,
                created_at INTEGER NOT NULL,
                retry_count INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        
        // Create index for sync queue processing
        db.execSQL("CREATE INDEX index_sync_queue_created_at ON sync_queue(created_at)")
    }
}