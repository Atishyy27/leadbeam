// feature/business/src/main/java/com/fieldflow/feature/business/ui/list/BusinessListUiState.kt
package com.fieldflow.feature.business.ui.list

import com.fieldflow.feature.business.domain.model.BusinessDetail

sealed class BusinessListUiState {
    object Loading : BusinessListUiState()
    data class Success(
        val businesses: List<BusinessDetail>,
        val hasMore: Boolean = false
    ) : BusinessListUiState()
    data class Error(val message: String) : BusinessListUiState()
}