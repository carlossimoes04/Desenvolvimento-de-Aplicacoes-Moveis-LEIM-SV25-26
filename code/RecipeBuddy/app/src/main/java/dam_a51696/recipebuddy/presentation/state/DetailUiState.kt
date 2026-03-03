package dam_a51696.recipebuddy.presentation.state

import dam_a51696.recipebuddy.domain.model.MealDetail

/**
 * UI State for Detail screen
 */
sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val meal: MealDetail) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}
