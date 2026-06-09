package dam_A51696.pantrychef.domain.repository

import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.model.RecipeDetail

interface RecipeRepository {
    suspend fun getRecipesByIngredient(ingredient: String): List<Recipe>
    suspend fun getRecipeById(id: String): RecipeDetail?
}
