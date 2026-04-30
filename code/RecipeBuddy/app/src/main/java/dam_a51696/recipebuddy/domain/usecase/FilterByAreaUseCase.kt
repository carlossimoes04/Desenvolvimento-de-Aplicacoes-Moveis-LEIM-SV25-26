package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.domain.model.Meal
import javax.inject.Inject

/**
 * Use case for filtering meals by their geographic area/origin.
 * 
 * This use case handles the interaction with the repository and maps the response to
 * the domain [Meal] model.
 * 
 * @property repository The [MealRepository] providing the data.
 */
class FilterByAreaUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the filtering by area.
     * 
     * @param area The name of the area (e.g., "Canadian").
     * @return A [Result] containing a list of [Meal] objects if successful.
     */
    suspend operator fun invoke(area: String): Result<List<Meal>> {
        return try {
            val result = repository.filterByArea(area)
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
