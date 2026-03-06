package dam_a51696.recipebuddy.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_a51696.recipebuddy.domain.usecase.GetMealDetailUseCase
import dam_a51696.recipebuddy.domain.usecase.IsFavoriteUseCase
import dam_a51696.recipebuddy.domain.usecase.AddToFavoritesUseCase
import dam_a51696.recipebuddy.domain.usecase.RemoveFromFavoritesUseCase
import dam_a51696.recipebuddy.domain.model.MealDetail
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
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite
    
    private var currentMealId: String? = null
    private var currentMealDetail: MealDetail? = null
    
    init {
        val mealId = savedStateHandle.get<String>("meal_id")
        if (!mealId.isNullOrEmpty()) {
            currentMealId = mealId
            loadMealDetail(mealId)
            observeFavoriteStatus(mealId)
        }
    }
    
    private fun observeFavoriteStatus(mealId: String) {
        viewModelScope.launch {
            isFavoriteUseCase(mealId).collect { isFav ->
                _isFavorite.value = isFav
            }
        }
    }
    
    private fun loadMealDetail(mealId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            
            val result = getMealDetailUseCase(mealId)
            
            result.onSuccess { mealDetail ->
                currentMealDetail = mealDetail
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
    
    fun toggleFavorite() {
        val mealDetail = currentMealDetail ?: return
        
        viewModelScope.launch {
            if (_isFavorite.value) {
                removeFromFavoritesUseCase(mealDetail.id)
            } else {
                addToFavoritesUseCase(mealDetail)
            }
        }
    }
}
