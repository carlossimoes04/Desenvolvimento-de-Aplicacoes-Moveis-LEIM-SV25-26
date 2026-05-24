package dam_A51696.pantrychef.domain.usecase

import dam_A51696.pantrychef.domain.model.RecipeDetail
import dam_A51696.pantrychef.domain.model.ShoppingItem
import dam_A51696.pantrychef.domain.repository.PantryRepository
import dam_A51696.pantrychef.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddMissingIngredientsUseCase @Inject constructor(
    private val pantryRepository: PantryRepository,
    private val shoppingRepository: ShoppingRepository
) {
    suspend operator fun invoke(recipeDetail: RecipeDetail) {
        // Fetch current pantry ingredients just once
        val pantryIngredients = pantryRepository.getAllIngredients().first()
        val pantryNames = pantryIngredients.map { it.name.lowercase() }

        // Find missing ingredients
        val missingIngredients = recipeDetail.ingredients.filter { (name, _) ->
            !pantryNames.contains(name.lowercase())
        }

        // Add each missing ingredient to the shopping list
        missingIngredients.forEach { (name, measure) ->
            val newItem = ShoppingItem(
                id = "", // Firebase will generate
                name = name,
                details = measure,
                isBought = false
            )
            shoppingRepository.addItem(newItem)
        }
    }
}
