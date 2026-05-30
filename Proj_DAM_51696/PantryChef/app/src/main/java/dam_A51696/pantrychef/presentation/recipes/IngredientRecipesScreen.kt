package dam_A51696.pantrychef.presentation.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.White
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class IngredientRecipesState {
    object Loading : IngredientRecipesState()
    data class Success(val recipes: List<Recipe>, val ingredientName: String) : IngredientRecipesState()
    data class Error(val message: String) : IngredientRecipesState()
}

@HiltViewModel
class IngredientRecipesViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow<IngredientRecipesState>(IngredientRecipesState.Loading)
    val state: StateFlow<IngredientRecipesState> = _state.asStateFlow()

    init {
        // recebe o nome do ingrediente que passou na navegação
        val ingredient = savedStateHandle.get<String>("ingredientName")
        if (ingredient != null) {
            fetchRecipes(ingredient)
        } else {
            _state.value = IngredientRecipesState.Error("Ingredient missing")
        }
    }

    private fun fetchRecipes(ingredientName: String) {
        viewModelScope.launch {
            try {
                // vai procurar todas as receitas para esse ingrediente
                val recipes = recipeRepository.getRecipesByIngredient(ingredientName)
                _state.value = IngredientRecipesState.Success(recipes, ingredientName)
            } catch (e: Exception) {
                _state.value = IngredientRecipesState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

@Composable
fun IngredientRecipesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    viewModel: IngredientRecipesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(containerColor = CreamBackground) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // cabeçalho do ecrã com botão de voltar
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp).background(White, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ForestGreen)
                }
                Spacer(modifier = Modifier.width(16.dp))

                // título dinâmico (ex: "Recipes with Rice")
                val title = if (state is IngredientRecipesState.Success) {
                    "Recipes with ${(state as IngredientRecipesState.Success).ingredientName.replaceFirstChar { it.uppercase() }}"
                } else "Loading..."

                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            }

            // grelha - mostra todas as receitas infinitamente para baixo
            when (state) {
                is IngredientRecipesState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ForestGreen) }
                is IngredientRecipesState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = (state as IngredientRecipesState.Error).message, color = Color.Red) }
                is IngredientRecipesState.Success -> {
                    val recipes = (state as IngredientRecipesState.Success).recipes
                    LazyVerticalGrid( // lista em grelha
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(recipes) { recipe ->
                            RecipeGridCard(recipe = recipe, onClick = { onNavigateToRecipe(recipe.idMeal) })
                        }
                    }
                }
            }
        }
    }
}