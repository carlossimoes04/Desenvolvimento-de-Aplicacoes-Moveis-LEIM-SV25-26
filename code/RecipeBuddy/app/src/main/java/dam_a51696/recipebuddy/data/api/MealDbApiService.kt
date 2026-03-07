package dam_a51696.recipebuddy.data.api

import dam_a51696.recipebuddy.data.models.AreaListResponse
import dam_a51696.recipebuddy.data.models.CategoryListResponse
import dam_a51696.recipebuddy.data.models.IngredientListResponse
import dam_a51696.recipebuddy.data.models.MealDetailResponse
import dam_a51696.recipebuddy.data.models.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for interacting with TheMealDB API.
 * 
 * This interface defines the endpoints for searching meals, retrieving meal details,
 * and listing various meal attributes like categories, areas, and ingredients.
 */
interface MealDbApiService {
    
    /**
     * Searches for meals by name.
     * 
     * @param query The meal name to search for.
     * @return A [MealResponse] containing the search results.
     */
    @GET("api/json/v1/1/search.php")
    suspend fun searchMeals(
        @Query("s") query: String
    ): MealResponse
    
    /**
     * Retrieves detailed information for a specific meal by its ID.
     * 
     * @param mealId The unique identifier of the meal.
     * @return A [MealDetailResponse] containing the meal details.
     */
    @GET("api/json/v1/1/lookup.php")
    suspend fun getMealDetail(
        @Query("i") mealId: String
    ): MealDetailResponse
    
    /**
     * Filters meals by their category.
     * 
     * @param category The category name (e.g., "Seafood").
     * @return A [MealResponse] containing meals in the specified category.
     */
    @GET("api/json/v1/1/filter.php")
    suspend fun filterByCategory(
        @Query("c") category: String
    ): MealResponse
    
    /**
     * Filters meals by their area (origin).
     * 
     * @param area The area name (e.g., "Canadian").
     * @return A [MealResponse] containing meals from the specified area.
     */
    @GET("api/json/v1/1/filter.php")
    suspend fun filterByArea(
        @Query("a") area: String
    ): MealResponse
    
    /**
     * Filters meals by a specific ingredient.
     * 
     * @param ingredient The ingredient name (e.g., "chicken_breast").
     * @return A [MealResponse] containing meals that use the specified ingredient.
     */
    @GET("api/json/v1/1/filter.php")
    suspend fun filterByIngredient(
        @Query("i") ingredient: String
    ): MealResponse
    
    /**
     * Retrieves a list of all available meal categories.
     * 
     * @return A [CategoryListResponse] containing all categories.
     */
    @GET("api/json/v1/1/list.php?c=list")
    suspend fun getAllCategories(): CategoryListResponse
    
    /**
     * Retrieves a list of all available meal areas.
     * 
     * @return A [AreaListResponse] containing all areas.
     */
    @GET("api/json/v1/1/list.php?a=list")
    suspend fun getAllAreas(): AreaListResponse
    
    /**
     * Retrieves a list of all available meal ingredients.
     * 
     * @return A [IngredientListResponse] containing all ingredients.
     */
    @GET("api/json/v1/1/list.php?i=list")
    suspend fun getAllIngredients(): IngredientListResponse
}
