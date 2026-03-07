package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.domain.model.Meal
import javax.inject.Inject

/**
 * Use case for filtering meals by their category.
 * 
 * This use case handles the interaction with the repository and maps the response to
 * the domain [Meal] model.
 * 
 * @property repository The [MealRepository] providing the data.
 */
class FilterByCategoryUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the filtering by category.
     * 
     * @param category The name of the category (e.g., "Seafood").
     * @return A [Result] containing a list of [Meal] objects if successful.
     */
    suspend operator fun invoke(category: String): Result<List<Meal>> {
        return try {
            val result = repository.filterByCategory(category)
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
