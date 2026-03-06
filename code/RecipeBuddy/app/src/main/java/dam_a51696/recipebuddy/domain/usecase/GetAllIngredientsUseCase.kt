package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import javax.inject.Inject

/**
 * Use case for getting all ingredients
 */
class GetAllIngredientsUseCase @Inject constructor(
    private val repository: MealRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return repository.getAllIngredients()
    }
}
