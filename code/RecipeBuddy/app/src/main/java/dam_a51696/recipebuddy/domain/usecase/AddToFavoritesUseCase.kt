package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.domain.model.MealDetail
import javax.inject.Inject

/**
 * Use case for adding a meal to favorites
 */
class AddToFavoritesUseCase @Inject constructor(
    private val repository: MealRepository
) {
    suspend operator fun invoke(meal: MealDetail) {
        repository.addToFavorites(meal)
    }
}
