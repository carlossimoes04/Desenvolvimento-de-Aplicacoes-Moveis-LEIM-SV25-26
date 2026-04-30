package dam_a51696.recipebuddy.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_a51696.recipebuddy.domain.usecase.GetAllFavoritesUseCase
import dam_a51696.recipebuddy.domain.usecase.RemoveFromFavoritesUseCase
import dam_a51696.recipebuddy.presentation.state.FavoritesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Favorites screen.
 * 
 * This ViewModel handles observing the list of favorite meals and provides
 * functionality to remove meals from the favorites list.
 * 
 * @property getAllFavoritesUseCase Use case to observe all favorite meals.
 * @property removeFromFavoritesUseCase Use case to remove a meal from favorites.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Empty)
    /**
     * State representing the current UI status for the favorites list (Empty, Success).
     */
    val uiState: StateFlow<FavoritesUiState> = _uiState
    
    init {
        observeFavorites()
    }
    
    /**
     * Starts observing the favorites Flow from the use case.
     */
    private fun observeFavorites() {
        viewModelScope.launch {
            getAllFavoritesUseCase().collect { favorites ->
                _uiState.value = if (favorites.isEmpty()) {
                    FavoritesUiState.Empty
                } else {
                    FavoritesUiState.Success(favorites)
                }
            }
        }
    }
    
    /**
     * Removes a specific meal from the favorites list.
     * 
     * @param mealId The unique ID of the meal to remove.
     */
    fun removeFromFavorites(mealId: String) {
        viewModelScope.launch {
            removeFromFavoritesUseCase(mealId)
        }
    }
}
