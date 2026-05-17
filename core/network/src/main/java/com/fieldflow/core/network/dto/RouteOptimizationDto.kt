// core/network/src/main/java/com/fieldflow/core/network/dto/RouteOptimizationDto.kt
package com.fieldflow.core.network.dto

import com.google.gson.annotations.SerializedName

data class RouteOptimizationResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: OptimizationData
)

data class OptimizationData(
    @SerializedName("route_id") val routeId: String,
    @SerializedName("optimized_stops") val optimizedStops: List<OptimizedStop>,
    @SerializedName("original_distance") val originalDistance: Double,
    @SerializedName("optimized_distance") val optimizedDistance: Double,
    @SerializedName("original_duration") val originalDuration: Int,
    @SerializedName("optimized_duration") val optimizedDuration: Int
)

data class OptimizedStop(
    @SerializedName("business_id") val businessId: String,
    @SerializedName("order_index") val orderIndex: Int,
    @SerializedName("name") val name: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("long") val long: Double
)