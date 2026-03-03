package dam_a51696.recipebuddy.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_a51696.recipebuddy.domain.usecase.SearchMealsUseCase
import dam_a51696.recipebuddy.presentation.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Search screen
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMealsUseCase: SearchMealsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState
    
    fun searchMeals(query: String) {
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }
        
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            
            val result = searchMealsUseCase(query)
            
            result.onSuccess { meals ->
                if (meals.isEmpty()) {
                    _uiState.value = SearchUiState.NoResults
                } else {
                    _uiState.value = SearchUiState.Success(meals)
                }
            }.onFailure { throwable ->
                _uiState.value = SearchUiState.Error(
                    throwable.message ?: "Unknown error occurred"
                )
            }
        }
    }
}
