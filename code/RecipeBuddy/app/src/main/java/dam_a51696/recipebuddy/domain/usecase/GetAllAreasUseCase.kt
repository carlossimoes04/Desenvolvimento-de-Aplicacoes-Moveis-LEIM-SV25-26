package dam_a51696.recipebuddy.domain.usecase

import dam_a51696.recipebuddy.data.repository.MealRepository
import javax.inject.Inject

/**
 * Use case for retrieving the list of all available meal areas (origins) from the repository.
 * 
 * @property repository The [MealRepository] providing the data.
 */
class GetAllAreasUseCase @Inject constructor(
    private val repository: MealRepository
) {
    /**
     * Executes the retrieval of all areas.
     * 
     * @return A [Result] containing a list of area names.
     */
    suspend operator fun invoke(): Result<List<String>> {
        return repository.getAllAreas()
    }
}
