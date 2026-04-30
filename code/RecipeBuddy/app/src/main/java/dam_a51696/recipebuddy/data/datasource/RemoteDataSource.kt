package dam_a51696.recipebuddy.data.datasource

import dam_a51696.recipebuddy.data.api.MealDbApiService
import dam_a51696.recipebuddy.data.models.AreaListResponse
import dam_a51696.recipebuddy.data.models.CategoryListResponse
import dam_a51696.recipebuddy.data.models.IngredientListResponse
import dam_a51696.recipebuddy.data.models.MealDetailResponse
import dam_a51696.recipebuddy.data.models.MealResponse
import javax.inject.Inject

/**
 * Remote data source that handles API calls to TheMealDB.
 * 
 * This class serves as a clean API for the repository to fetch data from the network.
 * 
 * @property apiService The Retrofit service for making API requests.
 */
class RemoteDataSource @Inject constructor(
    private val apiService: MealDbApiService
) {
    
    /**
     * Searches for meals by name.
     * 
     * @param query The search term.
     * @return [MealResponse] containing the results.
     */
    suspend fun searchMeals(query: String): MealResponse {
        return apiService.searchMeals(query)
    }
    
    /**
     * Fetches details for a specific meal.
     * 
     * @param mealId The unique identifier for the meal.
     * @return [MealDetailResponse] containing the detailed meal info.
     */
    suspend fun getMealDetail(mealId: String): MealDetailResponse {
        return apiService.getMealDetail(mealId)
    }
    
    /**
     * Filters meals by their category.
     * 
     * @param category The category name.
     * @return [MealResponse] containing filtered meals.
     */
    suspend fun filterByCategory(category: String): MealResponse {
        return apiService.filterByCategory(category)
    }
    
    /**
     * Filters meals by their area.
     * 
     * @param area The area name.
     * @return [MealResponse] containing filtered meals.
     */
    suspend fun filterByArea(area: String): MealResponse {
        return apiService.filterByArea(area)
    }
    
    /**
     * Filters meals by an ingredient.
     * 
     * @param ingredient The ingredient name.
     * @return [MealResponse] containing filtered meals.
     */
    suspend fun filterByIngredient(ingredient: String): MealResponse {
        return apiService.filterByIngredient(ingredient)
    }
    
    /**
     * Fetches all available meal categories.
     * 
     * @return [CategoryListResponse] with the full list.
     */
    suspend fun getAllCategories(): CategoryListResponse {
        return apiService.getAllCategories()
    }
    
    /**
     * Fetches all available meal areas.
     * 
     * @return [AreaListResponse] with the full list.
     */
    suspend fun getAllAreas(): AreaListResponse {
        return apiService.getAllAreas()
    }
    
    /**
     * Fetches all available meal ingredients.
     * 
     * @return [IngredientListResponse] with the full list.
     */
    suspend fun getAllIngredients(): IngredientListResponse {
        return apiService.getAllIngredients()
    }
}
