package dam_A51696.pantrychef.presentation.translator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.data.remote.api.ChatMessage
import dam_A51696.pantrychef.data.remote.api.ChatRequest
import dam_A51696.pantrychef.data.remote.api.NvidiaApi
import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.repository.FavoriteRepository
import dam_A51696.pantrychef.domain.repository.RecipeRepository
import dam_A51696.pantrychef.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsável por gerir a lógica de tradução de receitas através de IA
 *
 * Liga a interface gráfica aos repositórios de dados e à API da Nvidia,
 * controlando o histórico de mensagens, o estado de carregamento e a
 * lista de receitas favoritas do utilizador
 *
 * @param favoriteRepository repositório injetado para aceder às receitas guardadas
 * @param recipeRepository repositório injetado para obter os detalhes completos das receitas
 * @param nvidiaApi interface injetada para comunicar remotamente com a Nvidia
 */
@HiltViewModel
class TranslatorViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val recipeRepository: RecipeRepository,
    private val nvidiaApi: NvidiaApi
) : ViewModel() {

    // chave de autenticação para a API da Nvidia
    private val apiKey = "Bearer ${BuildConfig.NVIDIA_API_KEY}"

    // flow mutável privado que guarda o histórico de mensagens da conversa
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    // flow imutável público que a interface lê para desenhar as mensagens
    val messages: StateFlow<List<ChatMessage>> = _messages

    // controla a visibilidade do indicador de carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // guarda a lista de receitas que o utilizador marcou como favoritas
    private val _favorites = MutableStateFlow<List<Recipe>>(emptyList())
    val favorites: StateFlow<List<Recipe>> = _favorites

    // arranca automaticamente quando a viewmodel é instanciada
    init {
        viewModelScope.launch {
            // subscreve a lista de favoritos e atualiza o ecrã sempre que a base de dados mudar
            favoriteRepository.getFavorites().collect { list ->
                _favorites.value = list
            }
        }
    }

    /**
     * Reúne os dados de uma receita e solicita a sua tradução à IA
     *
     * Descarrega os detalhes completos da receita escolhida, formata
     * os ingredientes e instruções num texto de pedido e interage com
     * a api da Nvidia para receber e apresentar o resultado
     *
     * @param recipeSummary resumo da receita escolhida da lista de favoritos
     * @param targetLanguage idioma para o qual a receita deve ser traduzida
     */
    fun translateRecipe(recipeSummary: Recipe, targetLanguage: String) {
        viewModelScope.launch {
            // liga a roda de carregamento no ecrã
            _isLoading.value = true

            try {
                // pede a receita completa à api do the meal db usando o id
                val fullRecipe = recipeRepository.getRecipeById(recipeSummary.idMeal)

                // garante que a receita chegou sem problemas
                if (fullRecipe != null) {
                    // define a personalidade e as regras que a inteligência artificial deve seguir
                    val systemMsg = ChatMessage(
                        role = "system",
                        content = "You are a culinary translator bot. " +
                                "Translate the recipe accurately into the requested language."
                    )

                    // converte a lista de pares ingrediente/medida num texto com formato de lista
                    val ingredientesFormatados = fullRecipe.ingredients.joinToString(separator = "\n")
                    { par ->
                        "- ${par.second} ${par.first}"
                    }

                    // constrói a instrução complexa juntando todos os dados da receita
                    val userRequestText = """
                        Please translate this recipe into $targetLanguage.
                        
                        Title: ${fullRecipe.strMeal}
                        
                        Ingredients:
                        $ingredientesFormatados
                        
                        Instructions:
                        ${fullRecipe.strInstructions}
                    """.trimIndent() // remove os espaços em branco antes e após cada linha

                    val userMsg = ChatMessage(role = "user", content = userRequestText)

                    // adiciona um pequeno balão visual ao chat para dar
                    // feedback instantâneo ao utilizador
                    _messages.value += ChatMessage(
                                            "user",
                                            "Translate '${fullRecipe.strMeal}' to $targetLanguage.")

                    // empacota o pedido e atira para os servidores da nvidia
                    val request = ChatRequest(messages = listOf(systemMsg, userMsg))
                    val response = nvidiaApi.translateRecipe(apiKey, request)

                    // extrai a primeira resposta gerada e acrescenta-a ao histórico visual
                    val botReply = response.choices.first().message
                    _messages.value += botReply
                } else {
                    // caso a falha ocorra a descarregar os detalhes da receita
                    _messages.value += ChatMessage("assistant",
                                            "Error: Couldn't fetch the full recipe instructions from the server.")
                }

            } catch (e: Exception) {
                // apanha erros de rede como falta de internet ou demoras de resposta (timeout)
                _messages.value += ChatMessage("assistant", "Error: ${e.localizedMessage}")
            } finally {
                // desliga a roda de carregamento, independentemente de ter corrido bem ou mal
                _isLoading.value = false
            }
        }
    }
}

/*
 * Criei esta ViewModel para separar a lógica toda do código visual (o ecrã)
 *
 * Assim, a lógica de fazer os pedidos à internet fica aqui dentro e não suja o código do ecrã
 *
 * Decisões de Implementação:
 * - Injeção via Hilt:
 *      Usei o Hilt para injetar os Repositórios e a API pelo construtor. Isto tira
 *      muito peso à classe, porque não precisamos de criar ligações à internet à mão,
 *      permitindo que a ViewModel se foque apenas em tratar os dados.
 * - Estados Separados:
 *      Criei variáveis de estado (StateFlow) separadas para as mensagens (_messages), a roda
 *      a carregar (_isLoading) e as receitas (_favorites). Esta divisão garante que o ecrã
 *      só atualiza aquilo que precisa quando um dos dados muda, em vez de recarregar a página toda.
 * - Preparação Escondida do Texto:
 *      A junção dos ingredientes todos num texto e as regras do bot são feitas de forma
 *      escondida aqui no código para não encher o ecrã com mensagens gigantes. O utilizador
 *      só vê balões pequenos do estilo "Translate X to Y", mantendo o design do chat simples e limpo.
 */