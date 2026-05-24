package dam_A51696.pantrychef.domain.usecase

import dam_A51696.pantrychef.domain.model.Ingredient
import dam_A51696.pantrychef.domain.repository.PantryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetExpiringIngredientsUseCase @Inject constructor(
    private val pantryRepository: PantryRepository
) {
    // Ao usar "operator fun invoke", permite-se que esta classe seja chamada como se fosse uma função
    // Exemplo: getExpiringIngredientsUseCase(limit = 3)
    operator fun invoke(limit: Int = 3): Flow<List<Ingredient>> {
        
        // Procura-se a lista no repositório (que já vem ordenada do Firebase)
        return pantryRepository.getAllIngredients().map { allIngredients ->
            
            // Ignora-se ingredientes que já passaram da validade
            // e pega-se os primeiros N ingredientes da lista
            allIngredients.take(limit)
            
        }
    }
}