package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import javax.inject.Inject

/**
 * Use case for retrieving the list of all available meal ingredients from the repository.
 * 
 * @property repository The [MealRepository] providing the data.
 */
class GetAllIngredientsUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the retrieval of all ingredients.
     * 
     * @return A [Result] containing a list of ingredient names.
     */
    suspend operator fun invoke(): Result<List<String>> {
        return repository.getAllIngredients()
    }
}
