package dam_a51696.recipebuddy.presentation.state

import dam_a51696.recipebuddy.domain.model.MealDetail

/**
 * Represents the different UI states for the Favorites screen.
 */
sealed class FavoritesUiState {
    /**
     * State when no meals have been added to the favorites list.
     */
    data object Empty : FavoritesUiState()
    
    /**
     * State when the favorites list has been successfully retrieved and contains items.
     * 
     * @property favorites The list of favorited meals.
     */
    data class Success(val favorites: List<MealDetail>) : FavoritesUiState()
}
