package dam_a51696.recipebuddy.data.repository

import dam_a51696.recipebuddy.data.models.MealDetailResponse
import dam_a51696.recipebuddy.data.models.MealResponse

/**
 * Repository interface for meal data operations
 */
interface MealRepository {
    suspend fun searchMeals(query: String): Result<MealResponse>
    suspend fun getMealDetail(mealId: String): Result<MealDetailResponse>
}
