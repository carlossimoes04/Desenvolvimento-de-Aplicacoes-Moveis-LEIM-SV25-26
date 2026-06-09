package dam_A51696.pantrychef.domain.repository

import dam_A51696.pantrychef.domain.model.Ingredient
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações para gerir os ingredientes da despensa do utilizador
 *
 * Serve como contrato para a camada de apresentação interagir com a despensa
 */
interface PantryRepository {
    
    /**
     * Obtém todos os ingredientes da despensa em tempo real
     *
     * @return Um [Flow] contendo a lista de ingredientes do tipo [Ingredient]
     */
    fun getAllIngredients(): Flow<List<Ingredient>>
    
    /**
     * Adiciona um ingrediente à despensa
     *
     * @param ingredient O ingrediente a adicionar
     */
    suspend fun addIngredient(ingredient: Ingredient)
    
    /**
     * Atualiza os dados de um ingrediente na despensa
     *
     * @param ingredient O ingrediente a atualizar
     */
    suspend fun updateIngredient(ingredient: Ingredient)
    
    /**
     * Remove um ingrediente da despensa
     *
     * @param ingredient O ingrediente a remover
     */
    suspend fun deleteIngredient(ingredient: Ingredient)
}

/*
 * Criei esta interface na camada de domínio para gerir a despensa de ingredientes,
 * o que permite mudar o armazenamento de dados para o telemóvel no futuro sem precisar
 * de alterar os ViewModels
 *
 * A função getAllIngredients devolve um Flow porque a despensa sofre alterações
 * quando consumimos ou compramos itens e o ecrã precisa de mostrar as alterações
 * automaticamente
 *
 * As funções de adicionar, atualizar e remover são suspend porque fazem alterações de
 * um ingrediente de cada vez na base de dados, o que demora tempo a comunicar com a rede
 * e não pode bloquear a aplicação
 */
