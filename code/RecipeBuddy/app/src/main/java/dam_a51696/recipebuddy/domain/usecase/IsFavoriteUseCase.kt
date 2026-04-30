package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing whether a specific meal is marked as a favorite.
 * 
 * @property repository The [MealRepository] providing the data.
 */
class IsFavoriteUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the check for favorite status.
     * 
     * @param mealId The unique ID of the meal.
     * @return A Flow emitting true if the meal is a favorite, false otherwise.
     */
    operator fun invoke(mealId: String): Flow<Boolean> {
        return repository.isFavorite(mealId)
    }
}
