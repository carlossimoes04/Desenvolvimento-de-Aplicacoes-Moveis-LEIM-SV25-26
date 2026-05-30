package dam_A51696.pantrychef.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.repository.PantryRepository
import dam_A51696.pantrychef.domain.usecase.GetExpiringIngredientsUseCase
import dam_A51696.pantrychef.domain.usecase.GetRecipeDetailUseCase
import dam_A51696.pantrychef.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecipesUiState {
    object Loading : RecipesUiState()
    data class Success(
        val bestMatch: Recipe?,
        val bestMatchUsedIngredients: List<String>,
        val recipes: List<Recipe>
    ) : RecipesUiState()
    data class Error(val message: String) : RecipesUiState()
}

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val getExpiringIngredientsUseCase: GetExpiringIngredientsUseCase,
    private val getRecipesUseCase: GetRecipesUseCase,
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val pantryRepository: PantryRepository
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
                // procurar os ingredientes a expirar
                getExpiringIngredientsUseCase(3).collectLatest { ingredients ->
                    val ingredientNames = ingredients.map { it.name }
                    
                    // procurar a lista de receitas baseada nesses ingredientes
                    val recipes = getRecipesUseCase(ingredientNames)
                    
                    if (recipes.isNotEmpty()) {
                        val bestMatchRecipe = recipes.first()

                        // ler os detalhes da receita, para saber a lista de todos os ingredientes que ela precisa
                        val recipeDetail = getRecipeDetailUseCase(bestMatchRecipe.idMeal)

                        // ler a despensa | obtém-se toda a despensa | o .first() lê o Firebase apenas uma vez em vez de ficar à escuta
                        val allPantryItems = pantryRepository.getAllIngredients().first()

                        // comparar as duas listas
                        val matchingNames = mutableListOf<String>()
                        if (recipeDetail != null) {
                            // pega-se no nome dos ingredientes da receita e põe-se em minúsculas para comparar mais facilmente
                            val recipeIngredientsNames = recipeDetail.ingredients.map { it.first.lowercase() }

                            // para cada item da despensa do utilizador
                            allPantryItems.forEach { pantryItem ->
                                // vê-se se o nome do item da despensa faz parte dos ingredientes da receita
                                // ex: receita pede "Chicken Breast" e o utilizador tem "Chicken" -> match
                                if (recipeIngredientsNames.any { it.contains(pantryItem.name.lowercase()) }) {
                                    matchingNames.add(pantryItem.name)
                                }
                            }
                        }

                        // envia a lista para a UI
                        _uiState.value = RecipesUiState.Success(
                            bestMatch = bestMatchRecipe,
                            bestMatchUsedIngredients = matchingNames.distinct(), // Impede repetições
                            recipes = recipes.drop(1)
                        )
                    } else {
                        _uiState.value = RecipesUiState.Success(null, emptyList(), emptyList())
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RecipesUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
