package dam_A51696.pantrychef.domain.repository

import dam_A51696.pantrychef.domain.model.Recipe

interface RecipeRepository {
    suspend fun getRecipesByIngredient(ingredient: String): List<Recipe>
    suspend fun getRecipeById(id: String): dam_A51696.pantrychef.domain.model.RecipeDetail?
}
