package dam_A51696.pantrychef.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dam_A51696.pantrychef.data.remote.dto.IngredientFirebaseDto
import dam_A51696.pantrychef.data.remote.dto.toDomain
import dam_A51696.pantrychef.data.remote.dto.toDto
import dam_A51696.pantrychef.domain.model.Ingredient
import dam_A51696.pantrychef.domain.repository.PantryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.auth.FirebaseAuth

/**
 * Implementação do [PantryRepository] que utiliza o Firebase Realtime Database
 * para gerir os ingredientes da despensa do utilizador
 */
class PantryRepositoryImpl : PantryRepository {

    /**
     * Obtém a referência do Firebase para a despensa do utilizador
     *
     * @return [DatabaseReference] apontando para "users/$userId/pantry"
     */
    private fun getDatabaseRef(): DatabaseReference {
        // obtém o ID do utilizador ou usa um valor por defeito se não houver login
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unauthenticated"
        // retorna a referência da pasta de despensa do utilizador na base de dados
        return FirebaseDatabase.getInstance().getReference("users/$userId/pantry")
    }

    /**
     * Adiciona um ingrediente à despensa
     *
     * @param ingredient O ingrediente a adicionar
     */
    override suspend fun addIngredient(ingredient: Ingredient) {
        // obtém a referência da pasta de despensa
        val ref = getDatabaseRef()
        // cria uma referência para o ingrediente
        val newRef = ref.push()
        // converte o modelo em DTO e atribui o ID gerado pela Firebase
        val ingredientDto = ingredient.toDto().copy(id = newRef.key ?: "")
        // adiciona o ingrediente à base de dados
        newRef.setValue(ingredientDto) 
    }

    /**
     * Obtém todos os ingredientes da despensa em tempo real,
     * ordenados pela data de validade
     *
     * @return Um [Flow] contendo a lista de ingredientes
     */
    override fun getAllIngredients(): Flow<List<Ingredient>> = callbackFlow {
        // obtém a referência da pasta de despensa
        val ref = getDatabaseRef()
        // pede ao firebase para ordenar os dados pela data de validade
        val query = ref.orderByChild("expirationDate")
        // cria um listener para saber se os dados mudaram
        val listener = object : ValueEventListener {
            // executa sempre que os dados mudam no firebase
            override fun onDataChange(snapshot: DataSnapshot) {
                // mapeia os elementos
                val ingredients = snapshot.children.mapNotNull { 
                    it.getValue(IngredientFirebaseDto::class.java)?.toDomain() 
                }
                // envia a lista para o fluxo
                trySend(ingredients) 
            }
            // fecha o fluxo se ocorrer uma falha na leitura
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        // associa o listener à query de ordenação
        query.addValueEventListener(listener)
        // remove o listener quando o fluxo deixa de ser observado
        awaitClose { query.removeEventListener(listener) }
    }

    /**
     * Remove um ingrediente da despensa
     *
     * @param ingredient O ingrediente a remover
     */
    override suspend fun deleteIngredient(ingredient: Ingredient) {
        getDatabaseRef().child(ingredient.id).removeValue()
    }

    /**
     * Atualiza os dados de um ingrediente
     *
     * @param ingredient O ingrediente a atualizar
     */
    override suspend fun updateIngredient(ingredient: Ingredient) {
        getDatabaseRef().child(ingredient.id).setValue(ingredient.toDto())
    }
}

/*
 * Esta classe é a implementação do PantryRepository onde é gerida a despensa de ingredientes
 *
 * Para ler a lista utilizei callbackFlow com um ValueEventListener porque é necessário observar as
 * alterações de dados em tempo real, o que permite atualizar a interface sem recarregar o ecrã
 *
 * Adicionei a função orderByChild para receber os elementos ordenados por
 * validade diretamente da BD, de modo que a aplicação não tenha de
 * ordenar a lista na memória
 */