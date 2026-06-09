package dam_A51696.pantrychef.domain.usecase

import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.repository.RecipeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * Caso de uso para obter sugestões de receitas com base em ingredientes fornecidos
 *
 * @property recipeRepository Repositório para consultar as receitas na API
 */
class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    /**
     * Procura e agrega receitas que correspondam aos ingredientes passados
     *
     * Executa as consultas em paralelo para otimizar o tempo de carregamento de dados
     *
     * @param ingredients Lista com os nomes dos ingredientes a pesquisar
     * @return Lista de objetos [Recipe] misturados e sem repetições
     */
    suspend operator fun invoke(ingredients: List<String>): List<Recipe> {
        // devolve uma lista sem elementos se a entrada de ingredientes não tiver itens
        if (ingredients.isEmpty()) return emptyList()
        // o coroutineScope permite fazer múltiplos pedidos à API ao mesmo tempo (em paralelo)
        return coroutineScope {
            // cria-se um pedido assíncrono para CADA ingrediente da lista
            val deferredRecipes = ingredients.map { ingredientName ->
                async {
                    recipeRepository.getRecipesByIngredient(ingredientName)
                }
            }
            // aguarda a resposta de todos os pedidos à internet
            deferredRecipes.awaitAll()
                // junta as várias listas de receitas (uma por ingrediente) numa única lista gigante
                .flatten()
                // remove receitas duplicadas (ex: se frango e cenoura derem o mesmo prato)
                .distinctBy { it.idMeal }
                // baralha a ordem dos elementos da lista antes de devolver
                .shuffled()
        }
    }
}


/*
 * Criei este caso de uso para gerir a lógica de procura de sugestões com vários ingredientes
 *
 * Utilizei as funções async e awaitAll dentro de um coroutineScope porque preciso de realizar
 * os pedidos à API em paralelo, o que reduz o tempo de espera do utilizador em comparação
 * com pedidos feitos em sequência
 *
 * Apliquei as funções flatten, distinctBy e shuffled para agrupar e baralhar os resultados
 * na memória antes de os entregar à interface
 */