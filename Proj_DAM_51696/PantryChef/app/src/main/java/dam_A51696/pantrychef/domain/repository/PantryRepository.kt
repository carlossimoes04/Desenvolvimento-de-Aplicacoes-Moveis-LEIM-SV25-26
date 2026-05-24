package dam_A51696.pantrychef.domain.repository

import dam_A51696.pantrychef.domain.model.Ingredient
import kotlinx.coroutines.flow.Flow

interface PantryRepository {
    fun getAllIngredients(): Flow<List<Ingredient>>
    suspend fun addIngredient(ingredient: Ingredient)
    suspend fun updateIngredient(ingredient: Ingredient)
    suspend fun deleteIngredient(ingredient: Ingredient)
}
