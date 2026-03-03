package dam_a51696.recipebuddy.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_a51696.recipebuddy.domain.usecase.GetMealDetailUseCase
import dam_a51696.recipebuddy.presentation.state.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Detail screen
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMealDetailUseCase: GetMealDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState
    
    init {
        val mealId = savedStateHandle.get<String>("meal_id")
        if (!mealId.isNullOrEmpty()) {
            loadMealDetail(mealId)
        }
    }
    
    private fun loadMealDetail(mealId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            
            val result = getMealDetailUseCase(mealId)
            
            result.onSuccess { mealDetail ->
                _uiState.value = DetailUiState.Success(mealDetail)
            }.onFailure { throwable ->
                _uiState.value = DetailUiState.Error(
                    throwable.message ?: "Failed to load meal details"
                )
            }
        }
    }
    
    fun retry(mealId: String) {
        loadMealDetail(mealId)
    }
}
