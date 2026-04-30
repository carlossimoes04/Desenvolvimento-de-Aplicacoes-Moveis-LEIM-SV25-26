package dam_a51696.recipebuddy.presentation.state

import dam_a51696.recipebuddy.domain.model.Meal

/**
 * Representing the various UI states for the Search screen.
 */
sealed class SearchUiState {
    /**
     * Initial state before any search is performed.
     */
    data object Idle : SearchUiState()
    
    /**
     * State while a search or filter request is in progress.
     */
    data object Loading : SearchUiState()
    
    /**
     * State when a search or filter request succeeds with results.
     * 
     * @property meals The list of meals found.
     */
    data class Success(val meals: List<Meal>) : SearchUiState()
    
    /**
     * State when an error occurs during the search or filter process.
     * 
     * @property message The error message to display.
     */
    data class Error(val message: String) : SearchUiState()
    
    /**
     * State when the search or filter results are empty.
     */
    data object NoResults : SearchUiState()
}
