package dam_A51696.pantrychef.domain.repository

import dam_A51696.pantrychef.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações para gerir a lista de compras do utilizador
 *
 * Serve como contrato para a camada de apresentação interagir com a lista de compras
 */
interface ShoppingRepository {
    
    /**
     * Obtém todos os itens da lista de compras em tempo real
     *
     * @return Um [Flow] contendo a lista de itens de compra do tipo [ShoppingItem]
     */
    fun getAllItems(): Flow<List<ShoppingItem>>
    
    /**
     * Adiciona um item à lista de compras
     *
     * @param item O item a adicionar
     */
    suspend fun addItem(item: ShoppingItem)
    
    /**
     * Altera o estado de compra de um item (comprado/não comprado)
     *
     * @param item O item a alterar
     */
    suspend fun toggleItem(item: ShoppingItem)
    
    /**
     * Remove um item da lista de compras
     *
     * @param item O item a remover
     */
    suspend fun deleteItem(item: ShoppingItem)
}

/*
 * Criei esta interface na camada de domínio para gerir os itens da lista de compras sem ligar
 * as regras da aplicação ao Firebase, o que facilita trocar a sincronização na nuvem por
 * outra tecnologia de partilha no futuro
 *
 * A função getAllItems devolve um Flow porque a lista de compras precisa de reagir a
 * mudanças de estado como marcar itens como comprados e atualizar o ecrã instantaneamente
 *
 * As funções de adicionar, alterar e apagar são suspend porque efetuam operações de
 * escrita na base de dados, o que exige ligação à rede e não pode travar a navegação do
 * utilizador na aplicação
 */
