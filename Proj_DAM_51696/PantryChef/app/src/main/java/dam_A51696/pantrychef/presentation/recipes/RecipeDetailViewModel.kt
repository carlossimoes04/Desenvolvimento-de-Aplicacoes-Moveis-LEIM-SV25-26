package dam_A51696.pantrychef.presentation.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.model.RecipeDetail
import dam_A51696.pantrychef.domain.usecase.AddMissingIngredientsUseCase
import dam_A51696.pantrychef.domain.usecase.GetRecipeDetailUseCase
import dam_A51696.pantrychef.domain.repository.FavoriteRepository
import dam_A51696.pantrychef.domain.repository.PantryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado do ecrã dos detalhes da receita
 *
 * Guarda a situação atual: se está a carregar, se deu erro ou se já tem os dados
 */
sealed class RecipeDetailState {
    /**
     * Estado inicial enquanto espera pela resposta da api.
     */
    object Loading : RecipeDetailState()

    /**
     * Estado quando a app consegue carregar a receita inteira
     *
     * @param recipe o objeto com as instruções, ingredientes e dados todos
     */
    data class Success(val recipe: RecipeDetail) : RecipeDetailState()

    /**
     * Estado quando acontece algum erro a ir buscar a receita
     *
     * @param message mensagem de erro para mostrar à pessoa
     */
    data class Error(val message: String) : RecipeDetailState()
}

/**
 * ViewModel do ecrã dos detalhes da receita
 *
 * Obtém os dados pedidos à api, verifica se é favorito, lê os ingredientes
 * da despensa e deixa adicionar itens à lista de compras
 *
 * @param getRecipeDetailUseCase caso de uso para pedir a receita inteira à api
 * @param addMissingIngredientsUseCase caso de uso que trata da lógica de cruzar despensa com compras
 * @param favoriteRepository repositório para guardar ou apagar dos favoritos na base de dados
 * @param pantryRepository repositório para ler os ingredientes que temos na despensa
 * @param savedStateHandle guarda o id da receita que foi atirado na rota de navegação
 */
@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val addMissingIngredientsUseCase: AddMissingIngredientsUseCase,
    private val favoriteRepository: FavoriteRepository,
    private val pantryRepository: PantryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // estado privado e público que guarda os detalhes da receita
    private val _state = MutableStateFlow<RecipeDetailState>(RecipeDetailState.Loading)
    val state: StateFlow<RecipeDetailState> = _state.asStateFlow()
    // estado que avisa a interface se a receita é favorita ou não (coração)
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()
    // estado que guarda os nomes do que temos na despensa em minúsculas
    private val _pantryIngredients = MutableStateFlow<List<String>>(emptyList())
    val pantryIngredients = _pantryIngredients.asStateFlow()
    init {
        // tenta apanhar o id da receita a partir da navegação
        val recipeId = savedStateHandle.get<String>("recipeId")
        if (recipeId != null) {
            // se apanhou bem o id, começa as operações todas ao mesmo tempo
            fetchRecipeDetails(recipeId)
            observeFavoriteStatus(recipeId) // começa logo a verificar no firebase
            observePantry() // começa a ler a despensa
        } else {
            // se o id vier nulo, mostra erro
            _state.value = RecipeDetailState.Error("Recipe ID is missing")
        }
    }

    /**
     * Pede os detalhes completos da receita à api
     *
     * @param id identificador único da receita
     */
    private fun fetchRecipeDetails(id: String) {
        // corre a operação numa corrotina separada da interface
        viewModelScope.launch {
            _state.value = RecipeDetailState.Loading
            // chama o caso de uso
            val result = getRecipeDetailUseCase(id)
            if (result != null) {
                // se vierem dados, passa o estado para sucesso
                _state.value = RecipeDetailState.Success(result)
            } else {
                // se vier nulo da api, atira erro
                _state.value = RecipeDetailState.Error("Failed to load recipe details")
            }
        }
    }

    /**
     * Fica a escutar a base de dados para saber se o utilizador já tem isto nos favoritos
     *
     * @param recipeId identificador da receita a verificar
     */
    private fun observeFavoriteStatus(recipeId: String) {
        viewModelScope.launch {
            // fica à escuta e sempre que o valor no firebase mudar, atualiza o nosso estado
            favoriteRepository.isFavorite(recipeId).collect { isFavorite ->
                _isFavorite.value = isFavorite
            }
        }
    }

    /**
     * Lê a nossa despensa e guarda apenas os nomes numa lista em minúsculas
     * para a interface poder comparar facilmente e desenhar o "IN PANTRY"
     */
    private fun observePantry() {
        viewModelScope.launch {
            pantryRepository.getAllIngredients().collect { list ->
                // pega na lista de ingredientes e transforma numa lista só de nomes
                _pantryIngredients.value = list.map { it.name.lowercase() }
            }
        }
    }

    /**
     * Função chamada pelo botão verde gigante para meter na lista de compras o que falta
     */
    fun addMissingIngredients() {
        val currentState = _state.value
        // só deixa adicionar se a receita já tiver carregado com sucesso
        if (currentState is RecipeDetailState.Success) {
            viewModelScope.launch {
                // chama o caso de uso, que faz a magia de comparar tudo e enviar
                addMissingIngredientsUseCase(currentState.recipe)
            }
        }
    }

    /**
     * Função ativada quando o utilizador clica no ícone do coração
     *
     * Alterna entre meter nos favoritos ou tirar dos favoritos
     */
    fun toggleFavorite() {
        val currentState = _state.value
        // certifica-se de que os dados existem
        if (currentState is RecipeDetailState.Success) {
            val recipeDetail = currentState.recipe
            // como a api nos dá um RecipeDetail gigante, mas só se quer guardar
            // o formato pequeno na base de dados, converte-se o modelo aqui
            val recipeToSave = dam_A51696.pantrychef.domain.model.Recipe(
                idMeal = recipeDetail.idMeal,
                strMeal = recipeDetail.strMeal,
                strMealThumb = recipeDetail.strMealThumb
            )
            viewModelScope.launch {
                // se a receita já for favorita, manda o repositório apagar do firebase
                if (_isFavorite.value) {
                    favoriteRepository.removeFavorite(recipeDetail.idMeal)
                } else {
                    // se não for, manda o repositório guardar lá
                    favoriteRepository.addFavorite(recipeToSave)
                }
            }
        }
    }
}

/**
 * Criei este ficheiro para ser o ViewModel dos detalhes da receita
 *
 * Serve para ir buscar a receita inteira à API, verificar o que temos na despensa,
 * gerir se é um favorito ou não, e adicionar os ingredientes em falta à lista de compras
 *
 * Funções e componentes criados:
 * - RecipeDetailState:
 *      É uma classe selada (sealed class) que tem os três estados do ecrã: a carregar,
 *      com erro, ou com os dados da receita (Success)
 * - RecipeDetailViewModel:
 *      A classe principal que gere a lógica do ecrã. Recebe os vários repositórios
 *      e casos de uso injetados pelo Hilt
 * - fetchRecipeDetails:
 *      Função que pede os dados detalhados da receita à API usando o ID da navegação
 * - observeFavoriteStatus:
 *      Fica a escutar a base de dados em tempo real para saber se a receita está nos favoritos
 * - observePantry:
 *      Lê a lista da despensa e guarda os nomes em minúsculas para o ecrã saber o que já temos
 * - addMissingIngredients:
 *      Usa o caso de uso (UseCase) para comparar a receita com a despensa e enviar o
 *      que falta para a lista de compras
 * - toggleFavorite:
 *      Adiciona a receita à base de dados (Firebase) se ainda não for favorita,
 *      ou apaga se já for
 */