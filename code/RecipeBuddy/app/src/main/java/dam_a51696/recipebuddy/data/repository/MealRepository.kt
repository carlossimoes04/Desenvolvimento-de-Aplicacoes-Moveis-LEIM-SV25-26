package dam_a51696.recipebuddy.data.repository

import dam_a51696.recipebuddy.data.models.MealDetailResponse
import dam_a51696.recipebuddy.data.models.MealResponse
import dam_a51696.recipebuddy.domain.model.MealDetail
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining the data operations for meals.
 * 
 * This interface abstracts the data sources (remote and local) and provides
 * a unified way to fetch meal search results, details, filter options, and manage favorites.
 */
interface MealRepository {
    
    /**
     * Searches for meals by name.
     * 
     * @param query The search term.
     * @return A [Result] containing the [MealResponse].
     */
    suspend fun searchMeals(query: String): Result<MealResponse>
    
    /**
     * Fetches detailed information for a specific meal.
     * 
     * @param mealId The unique ID of the meal.
     * @return A [Result] containing the [MealDetailResponse].
     */
    suspend fun getMealDetail(mealId: String): Result<MealDetailResponse>
    
    /**
     * Filters meals by category.
     * 
     * @param category The category name.
     * @return A [Result] containing the [MealResponse].
     */
    suspend fun filterByCategory(category: String): Result<MealResponse>
    
    /**
     * Filters meals by area.
     * 
     * @param area The area name.
     * @return A [Result] containing the [MealResponse].
     */
    suspend fun filterByArea(area: String): Result<MealResponse>
    
    /**
     * Filters meals by ingredient.
     * 
     * @param ingredient The ingredient name.
     * @return A [Result] containing the [MealResponse].
     */
    suspend fun filterByIngredient(ingredient: String): Result<MealResponse>
    
    /**
     * Fetches all available meal categories.
     * 
     * @return A [Result] containing a list of category names.
     */
    suspend fun getAllCategories(): Result<List<String>>
    
    /**
     * Fetches all available meal areas.
     * 
     * @return A [Result] containing a list of area names.
     */
    suspend fun getAllAreas(): Result<List<String>>
    
    /**
     * Fetches all available meal ingredients.
     * 
     * @return A [Result] containing a list of ingredient names.
     */
    suspend fun getAllIngredients(): Result<List<String>>
    
    /**
     * Retrieves all favorite meals from local storage.
     * 
     * @return A Flow emitting a list of [MealDetail] objects.
     */
    fun getAllFavorites(): Flow<List<MealDetail>>
    
    /**
     * Retrieves a single favorite meal by its ID from local storage.
     * 
     * @param mealId The unique ID of the meal.
     * @return The [MealDetail] if it is a favorite, null otherwise.
     */
    suspend fun getFavoriteById(mealId: String): MealDetail?
    
    /**
     * Observes if a specific meal is marked as favorite.
     * 
     * @param mealId The unique ID of the meal.
     * @return A Flow emitting true if it is a favorite, false otherwise.
     */
    fun isFavorite(mealId: String): Flow<Boolean>
    
    /**
     * Adds a meal to the local favorites storage.
     * 
     * @param meal The [MealDetail] object to save.
     */
    suspend fun addToFavorites(meal: MealDetail)
    
    /**
     * Removes a meal from the local favorites storage.
     * 
     * @param mealId The unique ID of the meal to remove.
     */
    suspend fun removeFromFavorites(mealId: String)
    
    /**
     * Clears all favorite meals from local storage.
     */
    suspend fun clearAllFavorites()
}
