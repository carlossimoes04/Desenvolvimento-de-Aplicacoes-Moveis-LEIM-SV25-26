package dam_a51696.recipebuddy.data.repository

import dam_a51696.recipebuddy.data.datasource.RemoteDataSource
import dam_a51696.recipebuddy.data.models.MealDetailResponse
import dam_a51696.recipebuddy.data.models.MealResponse
import javax.inject.Inject

/**
 * Implementation of MealRepository
 */
class MealRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : MealRepository {
    
    override suspend fun searchMeals(query: String): Result<MealResponse> {
        return try {
            if (query.isBlank()) {
                Result.failure(IllegalArgumentException("Search query cannot be empty"))
            } else {
                val response = remoteDataSource.searchMeals(query)
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMealDetail(mealId: String): Result<MealDetailResponse> {
        return try {
            val response = remoteDataSource.getMealDetail(mealId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
