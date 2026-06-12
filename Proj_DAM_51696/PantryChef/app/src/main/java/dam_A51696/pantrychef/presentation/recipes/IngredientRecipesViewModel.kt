package dam_A51696.pantrychef.presentation.recipes

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

/**
 * Estado do ecrã de receitas por ingrediente
 *
 * Guarda a situação em que a interface se encontra (A carregar, Sucesso ou Erro)
 */
sealed class IngredientRecipesState {
    /**
     * Estado inicial de quando a app está à procura das receitas.
     */
    object Loading : IngredientRecipesState()
    /**
     * Estado usado quando a app encontra as receitas com sucesso
     *
     * @param recipes lista de receitas que vêm da api ou base de dados
     * @param ingredientName nome do ingrediente que pesquisei
     */
    data class Success(val recipes: List<Recipe>, val ingredientName: String) : IngredientRecipesState()
    /**
     * Estado de erro caso a pesquisa falhe
     *
     * @param message mensagem de erro para mostrar no ecrã
     */
    data class Error(val message: String) : IngredientRecipesState()
}

/**
 * ViewModel do ecrã de receitas por ingrediente
 *
 * Pesquisa as receitas no repositório e atualiza o estado para o ecrã desenhar
 *
 * @param recipeRepository repositório responsável por ir buscar as receitas
 * @param savedStateHandle guarda os parâmetros passados através da navegação
 */
@HiltViewModel
class IngredientRecipesViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // variável privada que guarda o estado atual (começa como loading)
    private val _state = MutableStateFlow<IngredientRecipesState>(IngredientRecipesState.Loading)
    // variável pública para o ecrã ler (não pode ser modificada por fora)
    val state: StateFlow<IngredientRecipesState> = _state.asStateFlow()

    init {
        // apanha o nome do ingrediente que passei na rota de navegação
        val ingredient = savedStateHandle.get<String>("ingredientName")
        if (ingredient != null) {
            // se existir ingrediente, começa a procurar as receitas
            fetchRecipes(ingredient)
        } else {
            // se houver erro e o ingrediente for nulo, muda o estado para erro
            _state.value = IngredientRecipesState.Error("Ingredient missing")
        }
    }

    /**
     * Vai procurar as receitas ao repositório usando uma corrotina
     *
     * @param ingredientName o nome do ingrediente a pesquisar
     */
    private fun fetchRecipes(ingredientName: String) {
        // lança uma corrotina atrelada ao viewmodel
        viewModelScope.launch {
            try {
                // pede as receitas ao repositório
                val recipes = recipeRepository.getRecipesByIngredient(ingredientName)
                // se correr bem, atualiza o estado para sucesso e guarda a lista e o nome
                _state.value = IngredientRecipesState.Success(recipes, ingredientName)
            } catch (e: Exception) {
                // se rebentar, atualiza o estado para erro e guarda a mensagem
                _state.value = IngredientRecipesState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

/**
 * Criei este ficheiro para ser o ViewModel do ecrã de Receitas por Ingrediente
 * Serve para pedir as receitas de um ingrediente específico à base de dados ou API,
 * gerindo o estado da pesquisa e enviando-o para a interface
 *
 * Funções e componentes criados:
 * - IngredientRecipesState:
 *      É uma classe selada (sealed class) que representa os três estados possíveis do ecrã:
 *      a carregar (Loading), com receitas (Success) e com erro (Error)
 * - IngredientRecipesViewModel:
 *      É o ViewModel deste ecrã. Lê o nome do ingrediente que passei na navegação
 *      através do savedStateHandle e faz a pesquisa no repositório
 * - fetchRecipes:
 *      Função dentro do ViewModel que lança uma corrotina para pedir as receitas à
 *      base de dados ou API e atualiza o estado
 */