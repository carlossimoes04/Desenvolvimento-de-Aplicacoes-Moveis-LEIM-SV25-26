package dam_A51696.pantrychef.presentation.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.model.Ingredient
import dam_A51696.pantrychef.domain.repository.PantryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel da despensa
 *
 * Gere os dados dos ingredientes e as ações do ecrã da despensa
 */
@HiltViewModel
class PantryViewModel @Inject constructor(
    // recebe o repositório para falar com a base de dados
    private val pantryRepository: PantryRepository
) : ViewModel() {

    // variável que guarda a lista de ingredientes sempre atualizada
    val ingredients: StateFlow<List<Ingredient>> = pantryRepository.getAllIngredients()
        .stateIn(
            // liga este fluxo ao tempo de vida do viewmodel
            scope = viewModelScope,
            // aguarda 5s antes de parar o fluxo para lidar com rotações de ecrã sem perder dados
            started = SharingStarted.WhileSubscribed(5000),
            // começa com uma lista vazia antes de carregar os dados
            initialValue = emptyList()
        )

    /**
     * Adiciona um novo ingrediente à base de dados
     *
     * @param name O nome do ingrediente
     * @param quantity A quantidade do ingrediente
     * @param unit A unidade do ingrediente
     * @param expirationDate A data de validade do ingrediente
     */
    fun addIngredient(name: String, quantity: Double, unit: String, expirationDate: Long) {
        // lança uma corrotina para não bloquear a interface
        viewModelScope.launch {
            // cria o objeto do novo ingrediente (o id vazio é gerado pela base de dados)
            val newIngredient = Ingredient(
                id = "",
                name = name,
                quantity = quantity,
                unit = unit,
                expirationDate = expirationDate
            )
            // guarda o ingrediente no repositório
            pantryRepository.addIngredient(newIngredient)
        }
    }

    /**
     * Atualiza os dados de um ingrediente que já existe
     *
     * @param ingredient O ingrediente com os novos dados
     */
    fun updateIngredient(ingredient: Ingredient) {
        // corre a operação em segundo plano
        viewModelScope.launch {
            // diz ao repositório para atualizar os dados
            pantryRepository.updateIngredient(ingredient)
        }
    }

    /**
     * Apaga um ingrediente da despensa
     *
     * @param ingredient O ingrediente a apagar
     */
    fun deleteIngredient(ingredient: Ingredient) {
        // corre a operação em segundo plano
        viewModelScope.launch {
            // diz ao repositório para apagar o ingrediente
            pantryRepository.deleteIngredient(ingredient)
        }
    }
}

/**
 * Criei este ficheiro para ser o ViewModel da despensa (Pantry)
 *
 * Serve para fazer a ponte entre a interface (PantryScreen) e a base de dados (PantryRepository),
 * gerindo a lista de ingredientes e as ações de adicionar, editar e apagar
 *
 * Funções e componentes criados:
 * - PantryViewModel:
 *      É a classe principal que gere o estado da despensa. Recebe o repositório por injeção
 *      de dependências do Hilt e guarda a lista de ingredientes
 * - ingredients:
 *      Variável que guarda a lista de ingredientes em tempo real usando um StateFlow, para que
 *      o ecrã atualize logo assim que houver mudanças na base de dados
 * - addIngredient:
 *      Função que recebe os dados de um novo ingrediente, cria um objeto Ingredient
 *      e guarda-o na base de dados
 * - updateIngredient:
 *      Função que recebe um ingrediente já com os dados alterados e envia para a base de dados
 *      para atualizar o registo
 * - deleteIngredient:
 *      Função que recebe um ingrediente e elimina-o da base de dados
 */