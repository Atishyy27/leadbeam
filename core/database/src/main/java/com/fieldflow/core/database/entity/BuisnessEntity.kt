package com.fieldflow.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "businesses",
    indices = [
        Index(value = ["latGrid", "longGrid"]),
        Index(value = ["category"])
    ]
)
data class BusinessEntity(
    @PrimaryKey val leadbeamId: String,
    val name: String,
    val lat: Double,
    val long: Double,
    val latGrid: Int,
    val longGrid: Int,
    val category: String,
    val isChain: Boolean,
    val rating: Double?,
    val overallConfidence: Double
)