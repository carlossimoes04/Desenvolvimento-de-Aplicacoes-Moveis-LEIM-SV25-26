package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for checking if a meal is favorited
 */
class IsFavoriteUseCase @Inject constructor(
    private val repository: MealRepository
) {
    operator fun invoke(mealId: String): Flow<Boolean> {
        return repository.isFavorite(mealId)
    }
}
