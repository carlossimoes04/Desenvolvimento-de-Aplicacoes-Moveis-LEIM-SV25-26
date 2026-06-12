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

/**
 * Estado visual do ecrã das receitas
 *
 * Reflete exatamente aquilo que a interface (RecipesScreen) deve mostrar ao utilizador
 */
sealed class RecipesUiState {
    /**
     * Estado ativado quando a aplicação está a carregar dados e precisa da roda verde
     */
    object Loading : RecipesUiState()

    /**
     * Estado ativado quando a operação é bem sucedida e já temos o que mostrar
     *
     * @param bestMatch a receita escolhida como a mais importante a apresentar
     * (pode ser nula)
     * @param bestMatchUsedIngredients os nomes dos ingredientes da despensa que a
     * receita principal usa
     * @param groupedRecipes um dicionário (map) que associa os nomes dos ingredientes
     * às respetivas listas de receitas
     * @param noRecipeIngredients ingredientes da despensa a expirar, mas para os quais
     * a API não encontrou nada
     */
    data class Success(
        val bestMatch: Recipe?,
        val bestMatchUsedIngredients: List<String>,
        // as receitas agora são guardadas como mapa -> ex: "Rice" = [Receita1, Receita2]
        val groupedRecipes: Map<String, List<Recipe>>,
        val noRecipeIngredients: List<String> = emptyList()
    ) : RecipesUiState()
    /**
     * Estado ativado caso ocorra um erro de ligação ou falha nos dados.
     *
     * @param message a string com a mensagem de erro a colocar no ecrã
     */
    data class Error(val message: String) : RecipesUiState()
}

/**
 * O ViewModel principal para o ecrã das receitas recomendadas
 *
 * É aqui que faço a lógica de cruzar os ingredientes da despensa que
 * estão a expirar com os resultados da API para sugerir as melhores
 * receitas ao utilizador
 *
 * @param getExpiringIngredientsUseCase caso de uso para obter os ingredientes mais urgentes da base de dados local
 * @param recipeRepository repositório que comunica com a API das receitas para as ir pesquisar
 * @param getRecipeDetailUseCase caso de uso para ir buscar os detalhes precisos de uma receita isolada à API
 * @param pantryRepository repositório global da despensa para cruzar ingredientes
 */
@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val getExpiringIngredientsUseCase: GetExpiringIngredientsUseCase,
    private val recipeRepository: RecipeRepository,
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val pantryRepository: PantryRepository
) : ViewModel() {

    // o estado principal que a UI vai ficar a ouvir
    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    /// variável que guarda num conjunto (set) os ingredientes que o utilizador escondeu manualmente
    // como começa vazio, significa que por defeito o ecrã vem com as categorias todas abertas
    private val _collapsedCategories = MutableStateFlow<Set<String>>(emptySet())
    val collapsedCategories: StateFlow<Set<String>> = _collapsedCategories.asStateFlow()

    /**
     * Alterna o estado visual de um grupo de receitas no ecrã
     *
     * Se estiver aberto fecha, se estiver fechado abre
     *
     * @param ingredient nome da categoria/ingrediente que o utilizador tocou
     */
    fun toggleCategory(ingredient: String) {
        // copia o estado atual
        val current = _collapsedCategories.value.toMutableSet()
        if (current.contains(ingredient)) {
            // se o ingrediente já estava na lista dos "escondidos",
            // tira-se de lá para ele abrir na UI
            current.remove(ingredient)
        } else {
            // se não estava, mete-se lá para o esconder
            current.add(ingredient)
        }
        // devolve-se o valor modificado para iniciar a recomposição do ecrã
        _collapsedCategories.value = current
    }

    init {
        // mal o ecrã abre e o viewmodel é instanciado, começa logo a ir buscar as receitas
        fetchRecipes()
    }

    /**
     * A função que orquestra as recomendações
     *
     * Vai ver o que está a expirar, pede as receitas à API para esses ingredientes,
     * escolhe uma receita principal, compara ingredientes e empacota tudo em grupos para o UI
     */
    private fun fetchRecipes() {
        // inicia-se uma corrotina para não travar o ecrã principal devido aos pedidos à API
        viewModelScope.launch {
            // começa por rodar a bolinha de carregamento
            _uiState.value = RecipesUiState.Loading
            try {
                // pede à base de dados local os 5 ingredientes mais perto de expirar
                getExpiringIngredientsUseCase(5).collectLatest { ingredients ->
                    // extrai só os nomes
                    val ingredientNames = ingredients.map { it.name }

                    val groupedMap = mutableMapOf<String, List<Recipe>>()
                    val emptyIngredients = mutableListOf<String>() // ingredientes que não têm receitas

                    // pedir as receitas individualmente por ingrediente e guardar no mapa
                    for (name in ingredientNames) {
                        val recipesForIngredient = recipeRepository.getRecipesByIngredient(name)
                        if (recipesForIngredient.isNotEmpty()) {
                            // se houver resultados, guarda no mapa com a chave = nome do ingrediente
                            groupedMap[name] = recipesForIngredient
                        } else {
                            // adiciona o ingrediente à lista de ingredientes sem receitas
                            emptyIngredients.add(name)
                        }
                    }

                    // se o mapa não estiver vazio (ou seja, apanhou-se pelo menos uma receita)
                    if (groupedMap.isNotEmpty()) {
                        // a Best Match passa a ser a primeira receita do ingrediente mais urgente (o primeiro do mapa)
                        val firstCategory = groupedMap.keys.first()
                        val bestMatchRecipe = groupedMap[firstCategory]!!.first()

                        // lógica de procurar quais ingredientes da despensa são usados pela Best Match
                        val recipeDetail = getRecipeDetailUseCase(bestMatchRecipe.idMeal)
                        val allPantryItems = pantryRepository.getAllIngredients().first()

                        val matchingNames = mutableListOf<String>()
                        if (recipeDetail != null) {
                            // metem-se todos em letras minúsculas para não falhar os "match" devido a maiúsculas
                            val recipeIngredientsNames = recipeDetail.ingredients.map { it.first.lowercase() }
                            allPantryItems.forEach { pantryItem ->
                                // se o ingrediente da receita conter uma palavra do que temos na despensa
                                if (recipeIngredientsNames.any { it.contains(pantryItem.name.lowercase()) }) {
                                    matchingNames.add(pantryItem.name)
                                }
                            }
                        }

                        // remover a Best Match de dentro do grupo para que não apareça repetida na "Discover More"
                        val updatedFirstCategoryList = groupedMap[firstCategory]!!.filter { it.idMeal != bestMatchRecipe.idMeal }
                        if (updatedFirstCategoryList.isEmpty()) {
                            // se o ingrediente só tinha aquela receita,
                            // o grupo fica vazio e apaga-se por completo
                            groupedMap.remove(firstCategory)
                        } else {
                            // senão, guardo a lista com menos 1 elemento
                            groupedMap[firstCategory] = updatedFirstCategoryList
                        }
                        // estado sucesso e envia-se para a interface
                        _uiState.value = RecipesUiState.Success(
                            bestMatch = bestMatchRecipe,
                            bestMatchUsedIngredients = matchingNames.distinct(),
                            groupedRecipes = groupedMap,
                            noRecipeIngredients = emptyIngredients
                        )
                    } else {
                        // caso não existam receitas nenhumas para
                        // nada do que estava a expirar

                        // então pede uma receita completamente aleatória à API
                        // para o ecrã não ficar vazio
                        val randomRecipe = recipeRepository.getRandomRecipe()
                        
                        val matchingNames = mutableListOf<String>()
                        if (randomRecipe != null) {
                            // tenta ver se existe algo na despensa que dê para essa receita aleatória
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

                        // atira um estado de sucesso mas vazio, apenas com a tal
                        // receita aleatória como "Best Match"
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

/**
 * Criei este ficheiro para ser o ViewModel das Receitas (Recipes)
 *
 * Serve para fazer a ponte entre a interface principal das receitas e os repositórios/casos de uso,
 * tratando de toda a lógica para agrupar as receitas por ingrediente e escolher a melhor recomendação
 *
 * Funções e componentes criados:
 * - RecipesUiState:
 *      É uma classe selada (sealed class) que gere os estados do ecrã (a carregar, com erro
 *      ou com os dados prontos no Success)
 * - RecipesViewModel:
 *      A classe principal que recebe tudo o que precisa por injeção do Hilt. Guarda o estado
 *      atual das receitas e o estado das categorias que o utilizador escondeu
 * - toggleCategory:
 *      Função que recebe o nome de um ingrediente e adiciona ou tira da lista de categorias
 *      escondidas (fechadas pelo utilizador)
 * - fetchRecipes:
 *      É a função maior do ficheiro. Usa corrotinas para ir buscar os 5 ingredientes mais
 *      urgentes, pede receitas para eles à API, isola a "Best Match" (verificando o que já
 *      temos na despensa para ela) e envia tudo organizado para a interface
 */
