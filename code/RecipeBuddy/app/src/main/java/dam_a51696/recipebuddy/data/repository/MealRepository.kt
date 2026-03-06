package dam_a51696.recipebuddy.data.repository

import dam_a51696.recipebuddy.data.models.MealDetailResponse
import dam_a51696.recipebuddy.data.models.MealResponse
import dam_a51696.recipebuddy.domain.model.MealDetail
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for meal data operations
 */
interface MealRepository {
    // Existing methods
    suspend fun searchMeals(query: String): Result<MealResponse>
    suspend fun getMealDetail(mealId: String): Result<MealDetailResponse>
    
    // Filter methods
    suspend fun filterByCategory(category: String): Result<MealResponse>
    suspend fun filterByArea(area: String): Result<MealResponse>
    suspend fun filterByIngredient(ingredient: String): Result<MealResponse>
    suspend fun getAllCategories(): Result<List<String>>
    suspend fun getAllAreas(): Result<List<String>>
    suspend fun getAllIngredients(): Result<List<String>>
    
    // Favorites methods
    fun getAllFavorites(): Flow<List<MealDetail>>
    suspend fun getFavoriteById(mealId: String): MealDetail?
    fun isFavorite(mealId: String): Flow<Boolean>
    suspend fun addToFavorites(meal: MealDetail)
    suspend fun removeFromFavorites(mealId: String)
    suspend fun clearAllFavorites()
}
