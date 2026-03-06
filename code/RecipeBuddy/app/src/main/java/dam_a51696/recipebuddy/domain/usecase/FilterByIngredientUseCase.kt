package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.domain.model.Meal
import javax.inject.Inject

/**
 * Use case for filtering meals by ingredient
 */
class FilterByIngredientUseCase @Inject constructor(
    private val repository: MealRepository
) {
    suspend operator fun invoke(ingredient: String): Result<List<Meal>> {
        return try {
            val result = repository.filterByIngredient(ingredient)
            result.mapCatching { response ->
                response.meals?.map { mealDto ->
                    Meal(
                        id = mealDto.idMeal,
                        name = mealDto.strMeal,
                        imageUrl = mealDto.strMealThumb
                    )
                } ?: emptyList()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
