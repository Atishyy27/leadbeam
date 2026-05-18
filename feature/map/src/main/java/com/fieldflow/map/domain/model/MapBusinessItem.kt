// feature/map/src/main/java/com/fieldflow/map/domain/model/MapBusinessItem.kt
package com.fieldflow.feature.map.domain.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class MapBusinessItem(
    val id: String,
    val businessName: String,
    val category: String,
    val location: LatLng
) : ClusterItem {
    
    // FIX: Removed explicit position property — causes JVM signature clash
    // ClusterItem.getPosition() is already accessible as .position in Kotlin
    // Kotlin automatically converts Java getX() methods to property syntax

    override fun getPosition(): LatLng = location
    override fun getTitle(): String = businessName
    override fun getSnippet(): String = category
    override fun getZIndex(): Float? = null
}