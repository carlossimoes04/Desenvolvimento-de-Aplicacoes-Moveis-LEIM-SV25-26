package dam_A51696.pantrychef.domain.usecase

import dam_A51696.pantrychef.domain.model.RecipeDetail
import dam_A51696.pantrychef.domain.model.ShoppingItem
import dam_A51696.pantrychef.domain.repository.PantryRepository
import dam_A51696.pantrychef.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para comparar os ingredientes de uma receita com os da despensa
 * e adicionar os elementos em falta à lista de compras
 *
 * @property pantryRepository Repositório para aceder à despensa
 * @property shoppingRepository Repositório para gerir a lista de compras
 */
class AddMissingIngredientsUseCase @Inject constructor(
    private val pantryRepository: PantryRepository,
    private val shoppingRepository: ShoppingRepository
) {

    /**
     * Executa a comparação entre os ingredientes da receita e da despensa,
     * adicionando os itens em falta à lista de compras
     *
     * @param recipeDetail Detalhes da receita contendo a lista de ingredientes
     */
    suspend operator fun invoke(recipeDetail: RecipeDetail) {
        // lê a lista de ingredientes da despensa uma vez
        val pantryIngredients = pantryRepository.getAllIngredients().first()
        // coloca os nomes dos ingredientes em minúsculas para ignorar
        // diferenças de tamanho de letra
        val pantryNames = pantryIngredients.map { it.name.lowercase() }

        // filtra a lista da receita para encontrar ingredientes em falta na despensa
        val missingIngredients = recipeDetail.ingredients.filter { (name, _) ->
            !pantryNames.contains(name.lowercase())
        }

        // percorre a lista dos ingredientes em falta
        missingIngredients.forEach { (name, measure) ->
            // cria o item da lista de compras sem ID para o Firebase gerar a chave
            val newItem = ShoppingItem(
                id = "", // Firebase irá gerar um ID único
                name = name,
                details = measure,
                isBought = false
            )
            // adiciona o item à lista de compras na base de dados
            shoppingRepository.addItem(newItem)
        }
    }
}

/*
 * Criei este caso de uso para isolar a regra que compara a despensa com uma receita
 * e atualiza a lista de compras
 *
 * Utilizei o construtor com @Inject para que o Hilt consiga instanciar esta classe e
 * entregá-la aos ViewModels sempre que for preciso
 *
 * Usei a função first para obter os ingredientes no momento do clique, o que evita
 * manter um listener a escutar a despensa durante o processo
 */
