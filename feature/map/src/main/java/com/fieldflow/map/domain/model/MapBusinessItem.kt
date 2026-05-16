package com.fieldflow.feature.map.domain.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class MapBusinessItem(
    val id: String,
    val businessName: String,
    val category: String,
    val location: LatLng
) : ClusterItem {
    override fun getPosition(): LatLng = location
    override fun getTitle(): String = businessName
    override fun getSnippet(): String = category
    override fun getZIndex(): Float? = null
}