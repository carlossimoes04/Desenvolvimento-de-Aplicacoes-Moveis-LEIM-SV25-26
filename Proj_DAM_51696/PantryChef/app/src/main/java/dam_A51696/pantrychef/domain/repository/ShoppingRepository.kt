package dam_A51696.pantrychef.domain.repository

import dam_A51696.pantrychef.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {
    fun getAllItems(): Flow<List<ShoppingItem>>
    suspend fun addItem(item: ShoppingItem)
    suspend fun toggleItem(item: ShoppingItem)
    suspend fun deleteItem(item: ShoppingItem)
}
