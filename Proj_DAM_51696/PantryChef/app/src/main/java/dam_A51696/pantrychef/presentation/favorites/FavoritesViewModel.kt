package dam_A51696.pantrychef.presentation.favorites

// imports necessários para o ViewModel e Hilt
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
// import do modelo Recipe
import dam_A51696.pantrychef.domain.model.Recipe
// imports para gerir fluxos de dados (StateFlow)
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
import dam_A51696.pantrychef.domain.repository.FavoriteRepository
import kotlinx.coroutines.launch

// uma interface selada que define os 3 estados possíveis do ecrã
sealed interface FavoritesUiState {
    // estado 1: a carregar dados da base de dados
    object Loading : FavoritesUiState
    // estado 2: sucesso ao carregar, contém a lista de receitas favoritas
    data class Success(val recipes: List<Recipe>) : FavoritesUiState
    // estado 3: ocorreu um erro ao carregar (ex: sem internet)
    data class Error(val message: String) : FavoritesUiState
}

/**
 * ViewModel responsável por gerir as receitas favoritas do utilizador e
 * disponibilizar o estado de carregamento correspondente à UI
 *
 * A anotação @HiltViewModel indica que esta classe é gerida pelo Dagger Hilt
 * (injeção de dependências)
 *
 * A classe ViewModel guarda o estado e sobrevive a rotações do ecrã
 *
 * @property favoriteRepository Repositório utilizado para gerir os dados das receitas favoritas
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    /**
     * _uiState é privado e mutável (MutableStateFlow), apenas o ViewModel o pode alterar
     */
    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)

    /**
     * Fluxo de estado público e apenas de leitura (StateFlow) que a UI (Ecrã) vai "escutar"
     */
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    // o bloco init corre automaticamente assim que o ViewModel é criado
    init {
        // ao iniciar, carrega as receitas do Firebase (Realtime Database)
        viewModelScope.launch {
            favoriteRepository.getFavorites().collect { favoritesList ->
                // o ecrã será atualizado em "tempo real" sempre que a base de dados mudar
                _uiState.value = FavoritesUiState.Success(favoritesList)
            }
        }
    }
}

/**
 * Desenvolvi esta ViewModel com o propósito de gerir a lista de receitas favoritas
 * do utilizador, sincronizando as alterações em tempo real diretamente com a UI
 *
 * Decisões de Implementação
 * - FavoritesUiState:
 *      Implementei uma sealed interface para representar os estados do ecrã de forma
 *      segura e exclusiva, permitindo à UI alternar facilmente entre o progresso de
 *      carregamento, a lista de receitas e possíveis erros
 * - Injeção do FavoriteRepository:
 *      Decidi injetar o repositório através do construtor utilizando o Hilt, promovendo
 *      o desacoplamento e a testabilidade da classe, visto que a ViewModel não conhece
 *      a tecnologia de armazenamento
 * - Fluxo Unidirecional de Dados:
 *      Utilizei um MutableStateFlow privado e expus uma cópia imutável (asStateFlow),
 *      garantindo que o ecrã não consegue alterar o estado sem passar pela ViewModel
 * - Recolha em Tempo Real (init):
 *      Iniciei a recolha do fluxo de favoritos dentro do bloco init, assegurando que,
 *      assim que a classe é instanciada, o escopo da corrotina subscreve a base de dados
 *      e atualiza a lista automaticamente a cada alteração
 */