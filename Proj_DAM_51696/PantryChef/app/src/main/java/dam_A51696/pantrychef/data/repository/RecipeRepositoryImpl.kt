package dam_A51696.pantrychef.data.repository

import dam_A51696.pantrychef.data.remote.api.MealDbApi
import dam_A51696.pantrychef.data.remote.dto.toDomain
import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.repository.RecipeRepository
import javax.inject.Inject

// Recebe a interface da API injetada pelo Hilt
class RecipeRepositoryImpl @Inject constructor(
    private val api: MealDbApi
) : RecipeRepository {

    override suspend fun getRecipesByIngredient(ingredient: String): List<Recipe> {
        return try {
            // Faz o pedido de rede usando o nome do ingrediente
            val response = api.getRecipesByIngredient(ingredient)
            
            // Se "meals" for nulo, a API não encontrou nada, devolve-se emptyList()
            // Caso contrário, mapeia-se o MealDto para Recipe puro
            response.meals?.map { it.toDomain() } ?: emptyList()
            
        } catch (e: Exception) {
            e.printStackTrace()
            // Se houver um erro (ex: telemóvel sem internet), evita-se que a app vá abaixo (Crash) e 
            // devolve-se apenas uma lista vazia por agora
            emptyList()
        }
    }

    override suspend fun getRecipeById(id: String): dam_A51696.pantrychef.domain.model.RecipeDetail? {
        return try {
            val response = api.getRecipeById(id)
            response.toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}