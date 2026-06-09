package dam_A51696.pantrychef.domain.usecase

import dam_A51696.pantrychef.domain.model.RecipeDetail
import dam_A51696.pantrychef.domain.repository.RecipeRepository
import javax.inject.Inject

/**
 * Caso de uso para obter os detalhes completos de uma receita a partir do seu identificador
 *
 * @property repository Repositório de receitas de onde são extraídos os detalhes
 */
class GetRecipeDetailUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Executa o pedido de detalhe de receita no repositório
     *
     * @param id O identificador da receita
     * @return O objeto [RecipeDetail] correspondente ou null se ocorrer uma falha na ligação
     */
    suspend operator fun invoke(id: String): RecipeDetail? {
        // encaminha o pedido com o identificador da refeição para o repositório obter os dados
        return repository.getRecipeById(id)
    }
}

/*
 * Criei esta classe para fazer a ponte de comunicação entre o ViewModel e o
 * repositório de receitas na recolha de dados de uma refeição
 *
 * Com a injeção do construtor via @Inject, o Hilt resolve a criação da classe sempre
 * que os ViewModels de detalhe são iniciados
 *
 * A função é suspend porque acede à rede para descarregar dados e devolve null
 * se ocorrerem falhas de ligação
 */
