package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.domain.model.MealDetail
import javax.inject.Inject

/**
 * Use case for adding a meal to the favorites list.
 * 
 * @property repository The [MealRepository] where the meal will be saved.
 */
class AddToFavoritesUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the addition of a meal to favorites.
     * 
     * @param meal The [MealDetail] object to be favorited.
     */
    suspend operator fun invoke(meal: MealDetail) {
        repository.addToFavorites(meal)
    }
}
