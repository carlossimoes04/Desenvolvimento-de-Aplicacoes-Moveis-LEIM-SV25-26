package dam_a51696.recipebuddy.data.api

import dam_a51696.recipebuddy.data.models.AreaListResponse
import dam_a51696.recipebuddy.data.models.CategoryListResponse
import dam_a51696.recipebuddy.data.models.IngredientListResponse
import dam_a51696.recipebuddy.data.models.MealDetailResponse
import dam_a51696.recipebuddy.data.models.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for TheMealDB API
 */
interface MealDbApiService {
    
    @GET("api/json/v1/1/search.php")
    suspend fun searchMeals(
        @Query("s") query: String
    ): MealResponse
    
    @GET("api/json/v1/1/lookup.php")
    suspend fun getMealDetail(
        @Query("i") mealId: String
    ): MealDetailResponse
    
    // Filter endpoints
    @GET("api/json/v1/1/filter.php")
    suspend fun filterByCategory(
        @Query("c") category: String
    ): MealResponse
    
    @GET("api/json/v1/1/filter.php")
    suspend fun filterByArea(
        @Query("a") area: String
    ): MealResponse
    
    @GET("api/json/v1/1/filter.php")
    suspend fun filterByIngredient(
        @Query("i") ingredient: String
    ): MealResponse
    
    // List endpoints (for populating filter options)
    @GET("api/json/v1/1/list.php?c=list")
    suspend fun getAllCategories(): CategoryListResponse
    
    @GET("api/json/v1/1/list.php?a=list")
    suspend fun getAllAreas(): AreaListResponse
    
    @GET("api/json/v1/1/list.php?i=list")
    suspend fun getAllIngredients(): IngredientListResponse
}
