// feature/business/src/main/java/com/fieldflow/feature/business/ui/detail/BusinessDetailUiState.kt
package com.fieldflow.feature.business.ui.detail

import com.fieldflow.feature.business.domain.model.BusinessDetail

sealed class BusinessDetailUiState {
    object Loading : BusinessDetailUiState()
    data class Success(val business: BusinessDetail) : BusinessDetailUiState()
    data class Error(val message: String) : BusinessDetailUiState()
}

sealed class BusinessDetailEvent {
    data class ShowSnackbar(val message: String) : BusinessDetailEvent()
    data class NavigateToDialer(val phoneNumber: String) : BusinessDetailEvent()
    data class NavigateToEmail(val email: String) : BusinessDetailEvent()
    data class NavigateToWebsite(val url: String) : BusinessDetailEvent()
    data class NavigateToMaps(val lat: Double, val long: Double) : BusinessDetailEvent()
    object NavigateBack : BusinessDetailEvent()
}