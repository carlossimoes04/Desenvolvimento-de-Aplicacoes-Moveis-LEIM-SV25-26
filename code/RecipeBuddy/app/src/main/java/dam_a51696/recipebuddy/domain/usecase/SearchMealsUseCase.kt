package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.domain.model.Meal
import javax.inject.Inject

/**
 * Use case for searching for meals by name.
 * 
 * This use case handles the search logic, mapping the repository response to a list of
 * domain [Meal] objects and wrapping the result in a [Result].
 * 
 * @property repository The [MealRepository] providing the data.
 */
class SearchMealsUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the search for meals.
     * 
     * @param query The meal name or keyword to search for.
     * @return A [Result] containing a list of [Meal] objects if successful.
     */
    suspend operator fun invoke(query: String): Result<List<Meal>> {
        return try {
            val result = repository.searchMeals(query)
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
