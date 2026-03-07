package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import javax.inject.Inject

/**
 * Use case for removing a meal from the favorites list.
 * 
 * @property repository The [MealRepository] where the meal will be removed from.
 */
class RemoveFromFavoritesUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the removal of a meal from favorites.
     * 
     * @param mealId The unique ID of the meal to be removed.
     */
    suspend operator fun invoke(mealId: String) {
        repository.removeFromFavorites(mealId)
    }
}
