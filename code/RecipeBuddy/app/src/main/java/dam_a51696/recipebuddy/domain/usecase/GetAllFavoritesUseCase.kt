package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.domain.model.MealDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all meals marked as favorites from the repository.
 * 
 * @property repository The [MealRepository] providing access to local storage.
 */
class GetAllFavoritesUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the retrieval of all favorite meals.
     * 
     * @return A Flow emitting a list of [MealDetail] objects.
     */
    operator fun invoke(): Flow<List<MealDetail>> {
        return repository.getAllFavorites()
    }
}
