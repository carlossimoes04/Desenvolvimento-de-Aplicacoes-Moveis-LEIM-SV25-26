package dam_A51696.pantrychef.domain.repository

import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.model.RecipeDetail

/**
 * Interface que define as operações para obter receitas e os respetivos detalhes
 *
 * Serve como contrato para a camada de apresentação interagir com a API de receitas
 */
interface RecipeRepository {
    
    /**
     * Procura receitas na API que contenham o ingrediente fornecido
     *
     * @param ingredient O nome do ingrediente para pesquisa
     * @return Uma lista de objetos [Recipe]
     */
    suspend fun getRecipesByIngredient(ingredient: String): List<Recipe>
    
    /**
     * Obtém o detalhe de uma receita na API através do seu identificador
     *
     * @param id O identificador da receita
     * @return O objeto [RecipeDetail] correspondente ou null se ocorrer um erro
     */
    suspend fun getRecipeById(id: String): RecipeDetail?

    /**
     * Obtém uma receita completamente aleatória da API
     */
    suspend fun getRandomRecipe(): Recipe?
}

/*
 * Criei esta interface na camada de domínio para gerir as pesquisas de receitas, o que
 * nos permite mudar a fonte de dados das refeições para outra API sem precisar de alterar
 * os ViewModels
 *
 * Ao contrário de outros repositórios do projeto, esta interface não expõe objetos do
 * tipo Flow porque a obtenção de receitas envolve pedidos à internet que ocorrem apenas
 * uma vez e não requerem atualizações em tempo real
 *
 * Ambas as funções são suspend porque comunicam com a rede para descarregar dados, o
 * que exige tempo de processamento e não pode causar paragens na aplicação
 */
