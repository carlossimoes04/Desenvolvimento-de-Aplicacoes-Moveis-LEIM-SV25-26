package dam_A51696.pantrychef.presentation.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.model.RecipeDetail
import dam_A51696.pantrychef.domain.usecase.AddMissingIngredientsUseCase
import dam_A51696.pantrychef.domain.usecase.GetRecipeDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecipeDetailState {
    object Loading : RecipeDetailState()
    data class Success(val recipe: RecipeDetail) : RecipeDetailState()
    data class Error(val message: String) : RecipeDetailState()
}

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val addMissingIngredientsUseCase: AddMissingIngredientsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<RecipeDetailState>(RecipeDetailState.Loading)
    val state: StateFlow<RecipeDetailState> = _state.asStateFlow()

    init {
        val recipeId = savedStateHandle.get<String>("recipeId")
        if (recipeId != null) {
            fetchRecipeDetails(recipeId)
        } else {
            _state.value = RecipeDetailState.Error("Recipe ID is missing")
        }
    }

    private fun fetchRecipeDetails(id: String) {
        viewModelScope.launch {
            _state.value = RecipeDetailState.Loading
            val result = getRecipeDetailUseCase(id)
            if (result != null) {
                _state.value = RecipeDetailState.Success(result)
            } else {
                _state.value = RecipeDetailState.Error("Failed to load recipe details")
            }
        }
    }

    fun addMissingIngredients() {
        val currentState = _state.value
        if (currentState is RecipeDetailState.Success) {
            viewModelScope.launch {
                addMissingIngredientsUseCase(currentState.recipe)
            }
        }
    }
}
