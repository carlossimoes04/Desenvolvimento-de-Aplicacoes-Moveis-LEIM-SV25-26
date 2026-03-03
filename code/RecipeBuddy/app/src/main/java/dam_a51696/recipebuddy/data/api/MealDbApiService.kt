package dam_a51696.recipebuddy.data.api

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
}
