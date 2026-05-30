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

// uma interface selada que define os 3 estados possíveis do nosso ecrã
sealed interface FavoritesUiState {
    // estado 1: a carregar dados da base de dados
    object Loading : FavoritesUiState
    // estado 2: sucesso ao carregar, contém a lista de receitas favoritas
    data class Success(val recipes: List<Recipe>) : FavoritesUiState
    // estado 3: ocorreu um erro ao carregar (ex: sem internet)
    data class Error(val message: String) : FavoritesUiState
}

// @HiltViewModel indica que esta classe é gerida pelo Dagger Hilt (injeção de dependências)
@HiltViewModel
// a classe ViewModel guarda o estado e sobrevive a rotações do ecrã
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    // _uiState é privado e mutável (MutableStateFlow), apenas o ViewModel o pode alterar
    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)

    // uiState é público e apenas de leitura (StateFlow), a UI (Ecrã) vai "escutar" estas mudanças
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