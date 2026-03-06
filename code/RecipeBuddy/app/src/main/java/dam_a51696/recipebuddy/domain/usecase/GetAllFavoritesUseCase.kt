package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.domain.model.MealDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all favorite meals
 */
class GetAllFavoritesUseCase @Inject constructor(
    private val repository: MealRepository
) {
    operator fun invoke(): Flow<List<MealDetail>> {
        return repository.getAllFavorites()
    }
}
