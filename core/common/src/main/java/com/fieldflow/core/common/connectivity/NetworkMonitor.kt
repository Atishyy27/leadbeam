// core/common/src/main/java/com/fieldflow/core/common/connectivity/NetworkMonitor.kt
package com.fieldflow.core.common.connectivity

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isOnline: Flow<Boolean>
    fun isCurrentlyOnline(): Boolean
}