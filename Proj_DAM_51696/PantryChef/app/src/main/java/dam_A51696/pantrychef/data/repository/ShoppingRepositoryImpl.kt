package dam_A51696.pantrychef.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dam_A51696.pantrychef.data.remote.dto.ShoppingItemFirebaseDto
import dam_A51696.pantrychef.data.remote.dto.toDomain
import dam_A51696.pantrychef.data.remote.dto.toDto
import dam_A51696.pantrychef.domain.model.ShoppingItem
import dam_A51696.pantrychef.domain.repository.ShoppingRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.auth.FirebaseAuth

/**
 * Implementação do [ShoppingRepository] que utiliza o Firebase Realtime Database
 * para gerir a lista de compras do utilizador
 */
class ShoppingRepositoryImpl : ShoppingRepository {

    /**
     * Obtém a referência do Firebase para a lista de compras do utilizador
     *
     * @return [DatabaseReference] apontando para "users/$userId/shopping_list"
     */
    private fun getDatabaseRef(): DatabaseReference {
        // obtém o id do utilizador ou usa valor por defeito se não houver login
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unauthenticated"
        // devolve a referência da lista de compras na base de dados
        return FirebaseDatabase.getInstance().getReference("users/$userId/shopping_list")
    }

    /**
     * Obtém todos os itens da lista de compras em tempo real
     *
     * @return Um [Flow] contendo a lista de itens de compra
     */
    override fun getAllItems(): Flow<List<ShoppingItem>> = callbackFlow {
        // obtém a referência da base de dados
        val ref = getDatabaseRef()

        // cria o listener para observar as alterações nos dados
        val listener = object : ValueEventListener {
            // executa sempre que os dados mudam no Firebase
            override fun onDataChange(snapshot: DataSnapshot) {
                // mapeia os elementos do snapshot para DTO e depois para o modelo
                val items = snapshot.children.mapNotNull {
                    it.getValue(ShoppingItemFirebaseDto::class.java)?.toDomain()
                }
                // envia a lista para o fluxo
                trySend(items)
            }
            // fecha o fluxo se ocorrer erro de leitura
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        // regista o listener na referência da base de dados
        ref.addValueEventListener(listener)

        // remove o listener para libertar recursos quando o fluxo deixa de ser escutado
        awaitClose { ref.removeEventListener(listener) }
    }

    /**
     * Adiciona um item à lista de compras
     *
     * @param item O item a adicionar
     */
    override suspend fun addItem(item: ShoppingItem) {
        // obtém a referência da base de dados
        val ref = getDatabaseRef()
        // cria um nó e gera uma chave de identificação no Firebase
        val newRef = ref.push()
        // converte o modelo em DTO e atribui o ID gerado pelo Firebase
        val itemDto = item.toDto().copy(id = newRef.key ?: "")
        // guarda o objeto no Firebase
        newRef.setValue(itemDto)
    }
    /**
     * Altera o estado de compra de um item
     *
     * @param item O item a alterar
     */
    override suspend fun toggleItem(item: ShoppingItem) {
        // inverte o estado de compra do item e converte para DTO
        val updatedDto = item.copy(isBought = !item.isBought).toDto()
        // atualiza os dados do item no Firebase
        getDatabaseRef().child(item.id).setValue(updatedDto)
    }
    /**
     * Remove um item da lista de compras
     *
     * @param item O item a remover
     */
    override suspend fun deleteItem(item: ShoppingItem) {
        // apaga o item e todas as suas informações da base de dados
        getDatabaseRef().child(item.id).removeValue()
    }
}

/*
 * Esta classe é a implementação do ShoppingRepository que gere a lista de compras
 *
 * Para ler os itens usei callbackFlow com um ValueEventListener porque é preciso
 * observar alterações de dados em tempo real, o que permite atualizar a interface
 * sem recarregar o ecrã
 * https://developer.android.com/kotlin/flow?hl=pt-br#callback
 *
 * Nas operações de escrita utilizo a referência da base de dados com o id
 * de cada elemento para atualizar ou remover dados sob chaves no Realtime Database
 */
