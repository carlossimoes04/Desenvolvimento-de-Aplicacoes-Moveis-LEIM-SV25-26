package dam_A51696.pantrychef.presentation.search

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
 * Estado visual do ecrã de pesquisa
 *
 * Guarda a situação em que nos encontramos para a interface saber o que desenhar
 */
sealed class SearchUiState {
    /**
     * Estado inicial de descanso, quando a pessoa ainda não tentou pesquisar nada
     */
    object Idle : SearchUiState()

    /**
     * Estado ativado assim que carregam na lupa e a app faz os pedidos à API
     */
    object Loading : SearchUiState()

    /**
     * Estado usado quando a API responde e já temos resultados para mostrar
     *
     * @param recipes lista de receitas encontradas para aquele ingrediente
     */
    data class Success(val recipes: List<Recipe>) : SearchUiState()

    /**
     * Estado ativado se houver uma falha de rede ou problema na API
     *
     * @param message texto que explica qual foi o erro
     */
    data class Error(val message: String) : SearchUiState()
}

/**
 * ViewModel responsável pela lógica do ecrã de pesquisa livre (SearchScreen)
 *
 * Fica a guardar o texto que está na barra e trata de pedir os dados ao repositório
 * quando se clica no botão de procurar
 *
 * @param recipeRepository repositório injetado pelo Hilt usado para fazer chamadas à API
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    // estado que guarda a situação da pesquisa, começa parado (Idle)
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    // estado que guarda o texto exato que está escrito na barra de pesquisa
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Atualiza o texto guardado sempre que a pessoa digita ou apaga uma letra
     *
     * @param query o novo texto completo que vem da barra de pesquisa
     */
    fun updateSearchQuery(query: String) {
        // substitui o valor atual pelo novo para o ecrã reagir logo
        _searchQuery.value = query
    }

    /**
     * Inicia a pesquisa a sério chamando a API das receitas
     */
    fun searchRecipes() {
        // apanha a palavra que estava escrita e tira-lhe os espaços em branco nos cantos
        val query = _searchQuery.value.trim()

        // se tiver vazio, não faz sentido ir à API gastar tráfego
        if (query.isEmpty()) return
        // lança uma corrotina associada a este viewmodel
        viewModelScope.launch {
            // avisa a interface que agora vamos ter de esperar (mostra a rodinha)
            _uiState.value = SearchUiState.Loading

            try {
                // pede ao repositório para pesquisar todas as receitas que levam este ingrediente
                val recipes = recipeRepository.getRecipesByIngredient(query)
                // se correr bem, atualiza o estado enviando os cartões de sucesso
                _uiState.value = SearchUiState.Success(recipes)
            } catch (e: Exception) {
                // se falhar e rebentar com uma exceção, atira a mensagem para o estado de erro
                _uiState.value = SearchUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

/**
 * Criei este ficheiro para ser o ViewModel da pesquisa manual
 *
 * Serve para guardar o texto que o utilizador escreve na barra, fazer
 * a ponte com o repositório da API e gerir os estados visuais (carregar, sucesso, erro)
 *
 * Funções e componentes criados:
 * - SearchUiState:
 *      É uma classe selada (sealed class) que define os quatro estados do ecrã de pesquisa:
 *      Idle (parado), Loading (a carregar), Success (com as receitas) e Error (com mensagem)
 * - SearchViewModel:
 *      A classe principal que herda de ViewModel. Recebe o repositório por injeção
 *      de dependências para não o instanciar à mão
 * - updateSearchQuery:
 *      Função simples chamada a cada letra que o utilizador escreve, atualizando o
 *      estado do texto para a interface reagir em tempo real
 * - searchRecipes:
 *      Função chamada quando se clica na lupa. Lê o texto atual, lança uma corrotina
 *      e faz o pedido das receitas à base de dados ou API, lidando com os erros
 */