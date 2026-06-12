package dam_A51696.pantrychef.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.model.ShoppingItem
import dam_A51696.pantrychef.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel da lista de compras
 *
 * É a ponte lógica entre o ecrã visual (ShoppingListScreen) e os dados (ShoppingRepository),
 * gerindo tudo o que é ver, adicionar, riscar ou apagar itens
 *
 * @param shoppingRepository repositório injetado pelo Hilt responsável por comunicar com a base de dados (Firebase)
 */
@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {
    // estado reativo que puxa a lista toda de compras do repositório
    // transforma o Flow normal num StateFlow para o ecrã conseguir ler
    val items: StateFlow<List<ShoppingItem>> = shoppingRepository.getAllItems()
        .stateIn(
            // ciclo de vida do ViewModel
            viewModelScope,
            // aguarda 5s antes de parar a conexão para lidar com possíveis rotações
            // de ecrã e afins
            SharingStarted.WhileSubscribed(5000),
            // começa com lista vazia
            emptyList())

    /**
     * Adiciona um item novo
     *
     * @param name nome do ingrediente a comprar (ex: "Leite")
     * @param details descrição ou quantidade (ex: "Manual entry")
     */
    fun addItem(name: String, details: String) {
        // se o utilizador não escreveu nada, não faz nada
        if (name.isBlank()) return
        // abre uma corrotina para não encravar a UI
        viewModelScope.launch {
            // manda o item para a base de dados (id vazio porque o firebase gera um)
            shoppingRepository.addItem(
                ShoppingItem(id = "", name = name, details = details, isBought = false)
            )
        }
    }

    /**
     * Alterna o estado de um item (se for comprado passa a por comprar, e vice-versa)
     *
     * @param item o objeto na lista em que a pessoa acabou de clicar
     */
    fun toggleItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.toggleItem(item)
        }
    }

    /**
     * Apaga um item da face da terra e da base de dados
     *
     * @param item o objeto que a pessoa quer eliminar de vez
     */
    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.deleteItem(item)
        }
    }
}

/**
 * Criei este ficheiro para ser o ViewModel da lista de compras
 *
 * Serve para tirar o trabalho pesado do ecrã e concentrar aqui as chamadas à base de dados,
 * mantendo a lista de compras sempre atualizada em tempo real de forma reativa
 *
 * Funções e componentes criados:
 * - ShoppingViewModel:
 *      É a classe principal que herda de ViewModel. Recebe o repositório da lista
 *      de compras por injeção de dependências (Hilt) para simplificar a vida
 * - items:
 *      É uma variável de estado (StateFlow) que está sempre ligada à base de dados.
 *      Sempre que há uma mudança lá fora, ela atualiza-se e o ecrã redesenha-se sozinho
 * - addItem:
 *      Recebe o nome e os detalhes, garante que não estão em branco,
 *      cria um ShoppingItem novinho em folha e manda-o para o repositório
 * - toggleItem:
 *      Recebe um item onde a pessoa clicou e manda o repositório atualizar o seu boolean
 *      "isBought" lá na nuvem
 * - deleteItem:
 *      Recebe um item da lista e manda apagá-lo definitivamente através do repositório
 */