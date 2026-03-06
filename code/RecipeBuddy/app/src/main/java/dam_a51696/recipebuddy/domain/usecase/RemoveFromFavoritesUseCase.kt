package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import javax.inject.Inject

/**
 * Use case for removing a meal from favorites
 */
class RemoveFromFavoritesUseCase @Inject constructor(
    private val repository: MealRepository
) {
    suspend operator fun invoke(mealId: String) {
        repository.removeFromFavorites(mealId)
    }
}
