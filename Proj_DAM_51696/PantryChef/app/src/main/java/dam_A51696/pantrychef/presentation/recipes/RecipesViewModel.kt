package dam_A51696.pantrychef.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.usecase.GetExpiringIngredientsUseCase
import dam_A51696.pantrychef.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecipesUiState {
    object Loading : RecipesUiState()
    data class Success(val bestMatch: Recipe?, val recipes: List<Recipe>) : RecipesUiState()
    data class Error(val message: String) : RecipesUiState()
}

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val getExpiringIngredientsUseCase: GetExpiringIngredientsUseCase,
    private val getRecipesUseCase: GetRecipesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        fetchRecipes()
    }

    private fun fetchRecipes() {
        viewModelScope.launch {
            _uiState.value = RecipesUiState.Loading
            try {
                // Collect expiring ingredients (Top 3)
                getExpiringIngredientsUseCase(3).collectLatest { ingredients ->
                    val ingredientNames = ingredients.map { it.name }
                    
                    // Fetch recipes based on ingredients
                    val recipes = getRecipesUseCase(ingredientNames)
                    
                    if (recipes.isNotEmpty()) {
                        _uiState.value = RecipesUiState.Success(
                            bestMatch = recipes.first(),
                            recipes = recipes.drop(1)
                        )
                    } else {
                        // Fallback or empty state if no ingredients/recipes
                        _uiState.value = RecipesUiState.Success(null, emptyList())
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RecipesUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
