package dam_A51696.pantrychef.domain.usecase

import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.repository.RecipeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    // Recebe uma lista com o nome dos ingredientes urgentes (ex: ["Frango", "Cenoura"])
    suspend operator fun invoke(ingredients: List<String>): List<Recipe> {
        if (ingredients.isEmpty()) return emptyList()

        // O coroutineScope permite-nos fazer múltiplos pedidos à API ao mesmo tempo (em paralelo)
        return coroutineScope {
            
            // Cria-se um pedido assíncrono para CADA ingrediente da lista
            val deferredRecipes = ingredients.map { ingredientName ->
                async {
                    recipeRepository.getRecipesByIngredient(ingredientName)
                }
            }
            
            // Espera-se que a internet devolva TODAS as respostas (awaitAll)
            deferredRecipes.awaitAll()
                .flatten() // Junta as várias listas de receitas (uma por ingrediente) numa única lista gigante
                .distinctBy { it.idMeal } // Remove receitas duplicadas (ex: se frango e cenoura derem o mesmo prato)
                .shuffled()
        }
    }
}