// feature/route/src/main/java/com/fieldflow/feature/route/ui/execution/RouteExecutionUiState.kt
package com.fieldflow.feature.route.ui.execution

import com.fieldflow.feature.route.data.model.RouteExecutionState

sealed class RouteExecutionUiState {
    object Loading : RouteExecutionUiState()
    object NoActiveRoute : RouteExecutionUiState()
    data class Active(val executionState: RouteExecutionState) : RouteExecutionUiState()
}

sealed class RouteExecutionEvent {
    data class ShowMessage(val message: String) : RouteExecutionEvent()
    data class NavigateToMaps(val lat: Double, val long: Double) : RouteExecutionEvent()
    object RouteCompleted : RouteExecutionEvent()
}