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
 * ViewModel for the Meal Detail screen.
 * 
 * This ViewModel handles fetching detailed meal information, tracking the favorite status
 * of the meal, and providing methods to toggle the favorite state.
 * 
 * @property getMealDetailUseCase Use case to fetch full meal details.
 * @property isFavoriteUseCase Use case to observe favorite status.
 * @property addToFavoritesUseCase Use case to save meal to favorites.
 * @property removeFromFavoritesUseCase Use case to remove meal from favorites.
 * @param savedStateHandle Handle to retrieve navigation arguments (e.g., "meal_id").
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
    /**
     * State representing the current UI status (Loading, Success, Error).
     */
    val uiState: StateFlow<DetailUiState> = _uiState
    
    private val _isFavorite = MutableStateFlow(false)
    /**
     * Flow emitting the favorite status of the current meal.
     */
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
    
    /**
     * Observes the favorite status of a specific meal from the local database.
     * 
     * @param mealId The ID of the meal to observe.
     */
    private fun observeFavoriteStatus(mealId: String) {
        viewModelScope.launch {
            isFavoriteUseCase(mealId).collect { isFav ->
                _isFavorite.value = isFav
            }
        }
    }
    
    /**
     * Fetches detailed information for a meal.
     * 
     * @param mealId The ID of the meal to load.
     */
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
    
    /**
     * Retries loading the meal details in case of failure.
     * 
     * @param mealId The ID of the meal to reload.
     */
    fun retry(mealId: String) {
        loadMealDetail(mealId)
    }
    
    /**
     * Toggles the favorite status of the current meal.
     * 
     * If the meal is already a favorite, it is removed; otherwise, it is saved.
     */
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
