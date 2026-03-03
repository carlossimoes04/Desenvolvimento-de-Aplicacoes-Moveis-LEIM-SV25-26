package dam_a51696.recipebuddy.presentation.state

import dam_a51696.recipebuddy.domain.model.Meal

/**
 * UI State for Search screen
 */
sealed class SearchUiState {
    data object Idle : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(val meals: List<Meal>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
    data object NoResults : SearchUiState()
}
