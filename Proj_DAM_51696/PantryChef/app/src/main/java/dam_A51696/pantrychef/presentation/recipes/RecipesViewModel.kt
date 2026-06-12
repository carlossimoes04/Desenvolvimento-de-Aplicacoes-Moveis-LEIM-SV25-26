package dam_A51696.pantrychef.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.repository.PantryRepository
import dam_A51696.pantrychef.domain.usecase.GetExpiringIngredientsUseCase
import dam_A51696.pantrychef.domain.usecase.GetRecipeDetailUseCase
import dam_A51696.pantrychef.domain.repository.RecipeRepository
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
        // as receitas agora são guardadas como mapa -> ex: "Rice" = [Receita1, Receita2]
        val groupedRecipes: Map<String, List<Recipe>>,
        val noRecipeIngredients: List<String> = emptyList()
    ) : RecipesUiState()
    data class Error(val message: String) : RecipesUiState()
}

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val getExpiringIngredientsUseCase: GetExpiringIngredientsUseCase,
    private val recipeRepository: RecipeRepository,
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val pantryRepository: PantryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    // guarda os ingredientes que o utilizador escondeu manualmente
    // como começa vazio, significa que por defeito vêm todos abertos
    private val _collapsedCategories = MutableStateFlow<Set<String>>(emptySet())
    val collapsedCategories: StateFlow<Set<String>> = _collapsedCategories.asStateFlow()

    fun toggleCategory(ingredient: String) {
        val current = _collapsedCategories.value.toMutableSet()
        if (current.contains(ingredient)) {
            current.remove(ingredient) // se já estava fechado, abre-o
        } else {
            current.add(ingredient) // se estava aberto, fecha-o
        }
        _collapsedCategories.value = current
    }

    init {
        fetchRecipes()
    }

    private fun fetchRecipes() {
        viewModelScope.launch {
            _uiState.value = RecipesUiState.Loading
            try {
                // procurar os top 5 ingredientes a expirar
                getExpiringIngredientsUseCase(5).collectLatest { ingredients ->
                    val ingredientNames = ingredients.map { it.name }

                    val groupedMap = mutableMapOf<String, List<Recipe>>()
                    val emptyIngredients = mutableListOf<String>() // ingredientes que não têm receitas

                    // pedir as receitas individualmente por ingrediente e guardar no mapa
                    for (name in ingredientNames) {
                        val recipesForIngredient = recipeRepository.getRecipesByIngredient(name)
                        if (recipesForIngredient.isNotEmpty()) {
                            groupedMap[name] = recipesForIngredient
                        } else {
                            // adiciona o ingrediente à lista de ingredientes sem receitas
                            emptyIngredients.add(name)
                        }
                    }

                    if (groupedMap.isNotEmpty()) {
                        // a Best Match passa a ser a primeira receita do ingrediente mais urgente (o primeiro do mapa)
                        val firstCategory = groupedMap.keys.first()
                        val bestMatchRecipe = groupedMap[firstCategory]!!.first()

                        // lógica de procurar quais ingredientes da despensa são usados pela Best Match
                        val recipeDetail = getRecipeDetailUseCase(bestMatchRecipe.idMeal)
                        val allPantryItems = pantryRepository.getAllIngredients().first()

                        val matchingNames = mutableListOf<String>()
                        if (recipeDetail != null) {
                            val recipeIngredientsNames = recipeDetail.ingredients.map { it.first.lowercase() }
                            allPantryItems.forEach { pantryItem ->
                                if (recipeIngredientsNames.any { it.contains(pantryItem.name.lowercase()) }) {
                                    matchingNames.add(pantryItem.name)
                                }
                            }
                        }

                        // remover a Best Match de dentro do grupo para que não apareça repetida na "Discover More"
                        val updatedFirstCategoryList = groupedMap[firstCategory]!!.filter { it.idMeal != bestMatchRecipe.idMeal }
                        if (updatedFirstCategoryList.isEmpty()) {
                            groupedMap.remove(firstCategory) // Se o grupo ficar vazio, apaga o grupo
                        } else {
                            groupedMap[firstCategory] = updatedFirstCategoryList
                        }
                        _uiState.value = RecipesUiState.Success(
                            bestMatch = bestMatchRecipe,
                            bestMatchUsedIngredients = matchingNames.distinct(),
                            groupedRecipes = groupedMap, // Passar o mapa para a UI!
                            noRecipeIngredients = emptyIngredients
                        )
                    } else {
                        // caso não existam receitas para os ingredientes a expirar
                        val randomRecipe = recipeRepository.getRandomRecipe()
                        
                        val matchingNames = mutableListOf<String>()
                        if (randomRecipe != null) {
                            val recipeDetail = getRecipeDetailUseCase(randomRecipe.idMeal)
                            val allPantryItems = pantryRepository.getAllIngredients().first()
                            if (recipeDetail != null) {
                                val recipeIngredientsNames = recipeDetail.ingredients.map { it.first.lowercase() }
                                allPantryItems.forEach { pantryItem ->
                                    if (recipeIngredientsNames.any { it.contains(pantryItem.name.lowercase()) }) {
                                        matchingNames.add(pantryItem.name)
                                    }
                                }
                            }
                        }

                        _uiState.value = RecipesUiState.Success(
                            bestMatch = randomRecipe,
                            bestMatchUsedIngredients = matchingNames.distinct(),
                            groupedRecipes = emptyMap(),
                            noRecipeIngredients = emptyIngredients
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RecipesUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
