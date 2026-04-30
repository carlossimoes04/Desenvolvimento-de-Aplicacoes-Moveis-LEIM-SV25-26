package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.domain.model.Ingredient
import dam_a51696.recipebuddy.domain.model.MealDetail
import javax.inject.Inject

/**
 * Use case for retrieving full details of a specific meal.
 * 
 * This use case handles fetching the meal details from the repository and mapping
 * the DTO to the domain [MealDetail] model, including processing the ingredient list.
 * 
 * @property repository The [MealRepository] providing the data.
 */
class GetMealDetailUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the retrieval of meal details.
     * 
     * @param mealId The unique ID of the meal.
     * @return A [Result] containing the [MealDetail] if successful.
     */
    suspend operator fun invoke(mealId: String): Result<MealDetail> {
        return try {
            val result = repository.getMealDetail(mealId)
            result.mapCatching { response ->
                val mealDto = response.meals?.firstOrNull()
                    ?: throw Exception("Meal not found")
                
                val ingredients = mealDto.getIngredientsWithMeasures().map { (name, measure) ->
                    Ingredient(
                        name = name ?: "",
                        measure = measure
                    )
                }
                
                MealDetail(
                    id = mealDto.idMeal,
                    name = mealDto.strMeal,
                    imageUrl = mealDto.strMealThumb,
                    instructions = mealDto.strInstructions,
                    ingredients = ingredients
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
