package dam_a51696.recipebuddy.presentation.state

import dam_a51696.recipebuddy.domain.model.MealDetail

/**
 * UI state for Favorites screen
 */
sealed class FavoritesUiState {
    data object Empty : FavoritesUiState()
    data class Success(val favorites: List<MealDetail>) : FavoritesUiState()
}
