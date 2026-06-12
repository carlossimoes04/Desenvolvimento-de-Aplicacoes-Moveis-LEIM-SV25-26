package dam_A51696.pantrychef.data.repository

import dam_A51696.pantrychef.data.remote.api.MealDbApi
import dam_A51696.pantrychef.data.remote.dto.toDomain
import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.repository.RecipeRepository
import dam_A51696.pantrychef.domain.model.RecipeDetail

/**
 * Implementação do [RecipeRepository] que utiliza a API [MealDbApi]
 * para obter receitas a partir da internet
 *
 * @property api Interface do Retrofit para realizar as chamadas de rede
 */
class RecipeRepositoryImpl (
    private val api: MealDbApi
) : RecipeRepository {

    /**
     * Procura receitas na API que contenham o ingrediente fornecido
     *
     * @param ingredient O nome do ingrediente para pesquisa
     * @return Uma lista de objetos [Recipe]
     */
    override suspend fun getRecipesByIngredient(ingredient: String): List<Recipe> {
        return try {
            // executa o pedido com o nome do ingrediente
            val response = api.getRecipesByIngredient(ingredient)
            // converte a lista de DTOs para o modelo de domínio ou devolve uma
            // lista sem elementos se a API não encontrar receitas
            response.meals?.map { it.toDomain() } ?: emptyList()

        } catch (e: Exception) {
            // regista a falha no terminal e devolve uma lista sem elementos
            // para evitar que a aplicação pare
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Obtém os detalhes de uma receita na API através do seu id
     *
     * @param id O identificador da receita
     * @return O objeto [RecipeDetail] correspondente ou null se ocorrer um erro
     */
    override suspend fun getRecipeById(id: String): RecipeDetail? {
        return try {
            // executa o pedido para obter os detalhes da receita
            val response = api.getRecipeById(id)
            // converte a resposta da API para o modelo de detalhes da receita
            response.toDomain()
        } catch (e: Exception) {
            // regista a falha no terminal e devolve null para evitar paragens na aplicação
            e.printStackTrace()
            null
        }
    }

    /**
     * Pede uma receita aleatória
     */
    override suspend fun getRandomRecipe(): Recipe? {
        return try {
            val response = api.getRandomRecipe()
            response.meals?.firstOrNull()?.toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

/*
 * Esta classe é a implementação do RecipeRepository, que utiliza a
 * interface MealDbApi para comunicar com a API de receitas
 *
 * As funções são suspend e as operações estão dentro de blocos try-catch porque
 * a ligação à internet ou a API podem falhar, o que evita paragens na aplicação
 * ao retornar uma lista sem elementos ou o valor null
 *
 * A interface MealDbApi é recebida no construtor para se poder
 * fazer os pedidos à rede sem instanciar o Retrofit dentro desta classe
 *
 * Aqui não se usam callbackFlows porque quando a resposta de um pedido é recebida,
 * a ligação termina, pelo que não existem mais callbacks para observar. Além disso,
 * como o Retrofit suporta coroutines, basta usar funções suspend (como foi implementado)
 * que devolvem a lista ou o detalhe diretamente, sem necessidade de utilizar callbackFlows
 * ou outros flows
 */