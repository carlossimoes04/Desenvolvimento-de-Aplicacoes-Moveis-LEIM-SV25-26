package dam_a51696.recipebuddy.data.datasource

import dam_a51696.recipebuddy.data.api.MealDbApiService
import dam_a51696.recipebuddy.data.models.AreaListResponse
import dam_a51696.recipebuddy.data.models.CategoryListResponse
import dam_a51696.recipebuddy.data.models.IngredientListResponse
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
    
    // Filter methods
    suspend fun filterByCategory(category: String): MealResponse {
        return apiService.filterByCategory(category)
    }
    
    suspend fun filterByArea(area: String): MealResponse {
        return apiService.filterByArea(area)
    }
    
    suspend fun filterByIngredient(ingredient: String): MealResponse {
        return apiService.filterByIngredient(ingredient)
    }
    
    // List fetch methods
    suspend fun getAllCategories(): CategoryListResponse {
        return apiService.getAllCategories()
    }
    
    suspend fun getAllAreas(): AreaListResponse {
        return apiService.getAllAreas()
    }
    
    suspend fun getAllIngredients(): IngredientListResponse {
        return apiService.getAllIngredients()
    }
}
