// core/database/src/main/java/com/fieldflow/core/database/migrations/Migration_2_3.kt
package com.fieldflow.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create routes table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS routes (
                id TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                date TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                is_completed INTEGER NOT NULL DEFAULT 0,
                total_distance REAL,
                total_duration INTEGER
            )
        """.trimIndent())
        
        // Create route_stops table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS route_stops (
                id TEXT PRIMARY KEY NOT NULL,
                route_id TEXT NOT NULL,
                business_id TEXT NOT NULL,
                order_index INTEGER NOT NULL,
                is_visited INTEGER NOT NULL DEFAULT 0,
                visited_at INTEGER,
                notes TEXT,
                FOREIGN KEY(route_id) REFERENCES routes(id) ON DELETE CASCADE,
                FOREIGN KEY(business_id) REFERENCES businesses(leadbeam_id) ON DELETE CASCADE
            )
        """.trimIndent())
        
        // Create indexes
        db.execSQL("CREATE INDEX index_route_stops_route_id ON route_stops(route_id)")
        db.execSQL("CREATE INDEX index_route_stops_business_id ON route_stops(business_id)")
        db.execSQL("CREATE INDEX index_routes_date ON routes(date)")
    }
}