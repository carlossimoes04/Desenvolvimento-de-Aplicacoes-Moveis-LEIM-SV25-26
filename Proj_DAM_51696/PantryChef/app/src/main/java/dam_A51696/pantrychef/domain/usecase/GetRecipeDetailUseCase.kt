package dam_A51696.pantrychef.domain.usecase

import dam_A51696.pantrychef.domain.model.RecipeDetail
import dam_A51696.pantrychef.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipeDetailUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: String): RecipeDetail? {
        return repository.getRecipeById(id)
    }
}
