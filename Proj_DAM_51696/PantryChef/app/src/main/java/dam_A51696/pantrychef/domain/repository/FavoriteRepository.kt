package dam_A51696.pantrychef.domain.repository

import dam_A51696.pantrychef.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavorites(): Flow<List<Recipe>> // Lê tudo
    suspend fun addFavorite(recipe: Recipe) // Adiciona
    suspend fun removeFavorite(recipeId: String) // Remove
    fun isFavorite(recipeId: String): Flow<Boolean> // verifica se é favorito (em tempo real)
}