package dam_a51696.recipebuddy.presentation.state

import dam_a51696.recipebuddy.domain.model.MealDetail

/**
 * Represents the different UI states for the Meal Detail screen.
 */
sealed class DetailUiState {
    /**
     * State while fetching meal details from the source.
     */
    data object Loading : DetailUiState()
    
    /**
     * State when meal details have been successfully retrieved.
     * 
     * @property meal The detailed information of the meal.
     */
    data class Success(val meal: MealDetail) : DetailUiState()
    
    /**
     * State when an error occurs while fetching meal details.
     * 
     * @property message The error message to display.
     */
    data class Error(val message: String) : DetailUiState()
}
