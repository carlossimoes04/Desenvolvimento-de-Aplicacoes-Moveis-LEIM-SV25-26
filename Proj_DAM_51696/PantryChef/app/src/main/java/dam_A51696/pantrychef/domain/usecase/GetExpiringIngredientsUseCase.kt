package dam_A51696.pantrychef.domain.usecase

import dam_A51696.pantrychef.domain.model.Ingredient
import dam_A51696.pantrychef.domain.repository.PantryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obter os ingredientes da despensa mais próximos da data de validade
 *
 * @property pantryRepository Repositório para aceder aos dados da despensa
 */
class GetExpiringIngredientsUseCase @Inject constructor(
    private val pantryRepository: PantryRepository
) {
    /**
     * Executa a obtenção da lista de ingredientes que expiram primeiro, respeitando o limite
     *
     * @param limit A quantidade máxima de ingredientes a retornar
     * @return Um [Flow] contendo a lista de ingredientes
     */
    operator fun invoke(limit: Int = 3): Flow<List<Ingredient>> {
        // obtém o fluxo de ingredientes e aplica uma transformação à lista
        return pantryRepository.getAllIngredients().map { allIngredients ->
            // seleciona a quantidade de elementos definida no parâmetro limit
            allIngredients.take(limit)
            
        }
    }
}

/*
 * Criei este caso de uso para isolar a regra que seleciona os ingredientes que expiram primeiro
 *
 * Utilizo o operador invoke para chamar a classe diretamente como se fosse uma função para
 * simplificar o código nos ViewModels
 *
 * O repositório já devolve os dados ordenados por validade a partir do Firebase, pelo que
 * apenas aplico a função take para obter a quantidade de elementos que preciso sem realizar
 * ordenações na memória da aplicação
 */