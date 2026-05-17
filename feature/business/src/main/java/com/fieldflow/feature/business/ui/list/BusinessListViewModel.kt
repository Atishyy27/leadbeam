// feature/business/src/main/java/com/fieldflow/feature/business/ui/list/BusinessListViewModel.kt
package com.fieldflow.feature.business.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldflow.feature.business.data.repository.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessListViewModel @Inject constructor(
    private val repository: BusinessRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<BusinessListUiState>(BusinessListUiState.Loading)
    val uiState: StateFlow<BusinessListUiState> = _uiState.asStateFlow()
    
    private var currentSortBy = "name"
    private var currentPage = 0
    private val pageSize = 50
    
    init {
        loadBusinesses()
    }
    
    private fun loadBusinesses(reset: Boolean = false) {
        if (reset) currentPage = 0
        
        viewModelScope.launch {
            try {
                repository.getAllBusinesses(
                    sortBy = currentSortBy,
                    limit = pageSize,
                    offset = currentPage * pageSize
                ).collect { businesses ->
                    val currentState = _uiState.value
                    val allBusinesses = if (reset || currentState !is BusinessListUiState.Success) {
                        businesses
                    } else {
                        currentState.businesses + businesses
                    }
                    
                    _uiState.value = BusinessListUiState.Success(
                        businesses = allBusinesses,
                        hasMore = businesses.size >= pageSize
                    )
                }
            } catch (e: Exception) {
                _uiState.value = BusinessListUiState.Error(e.message ?: "Failed to load businesses")
            }
        }
    }
    
    fun setSortBy(sortBy: String) {
        currentSortBy = sortBy
        loadBusinesses(reset = true)
    }
    
    fun loadMore() {
        val currentState = _uiState.value
        if (currentState is BusinessListUiState.Success && currentState.hasMore) {
            currentPage++
            loadBusinesses()
        }
    }
}