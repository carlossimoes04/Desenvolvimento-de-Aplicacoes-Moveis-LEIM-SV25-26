package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.domain.model.Meal
import javax.inject.Inject

/**
 * Use case for filtering meals by a specific ingredient.
 * 
 * This use case handles the interaction with the repository and maps the response to
 * the domain [Meal] model.
 * 
 * @property repository The [MealRepository] providing the data.
 */
class FilterByIngredientUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the filtering by ingredient.
     * 
     * @param ingredient The name of the ingredient (e.g., "chicken_breast").
     * @return A [Result] containing a list of [Meal] objects if successful.
     */
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
