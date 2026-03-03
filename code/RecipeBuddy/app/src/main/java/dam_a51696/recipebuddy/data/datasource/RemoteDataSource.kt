package dam_a51696.recipebuddy.data.datasource

import dam_a51696.recipebuddy.data.api.MealDbApiService
import dam_a51696.recipebuddy.data.models.MealDetailResponse
import dam_a51696.recipebuddy.data.models.MealResponse
import javax.inject.Inject

/**
 * Remote data source for fetching data from TheMealDB API
 */
class RemoteDataSource @Inject constructor(
    private val apiService: MealDbApiService
) {
    
    suspend fun searchMeals(query: String): MealResponse {
        return apiService.searchMeals(query)
    }
    
    suspend fun getMealDetail(mealId: String): MealDetailResponse {
        return apiService.getMealDetail(mealId)
    }
}
