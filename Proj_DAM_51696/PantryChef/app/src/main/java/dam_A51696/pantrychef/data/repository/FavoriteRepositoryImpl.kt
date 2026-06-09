package dam_A51696.pantrychef.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dam_A51696.pantrychef.data.remote.dto.FavoriteRecipeFirebaseDto
import dam_A51696.pantrychef.data.remote.dto.toDomain
import dam_A51696.pantrychef.data.remote.dto.toFavoriteDto
import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.domain.repository.FavoriteRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


/**
 * Implementação do [FavoriteRepository] que utiliza o Firebase Realtime Database
 * para gerir as receitas favoritas do utilizador
 */
class FavoriteRepositoryImpl : FavoriteRepository {

    /**
     * Obtém a referência do Firebase para a pasta de favoritos do utilizador
     *
     * @return [DatabaseReference] apontando para "users/$userId/favorites"
     */
    private fun getDatabaseRef(): DatabaseReference {
        // obtém o id do utilizador ou usa um valor por defeito se não houver login
        // ? - significa que pode ser null
        // ?: - se o valor à esquerda for null, retorna o valor à direita
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unauthenticated"

        // retorna a referência da pasta de favoritos do utilizador na base de dados
        return FirebaseDatabase.getInstance().getReference("users/$userId/favorites")
    }

    /**
     * Obtém as receitas favoritas do utilizador em tempo real
     *
     * @return Um [Flow] contendo a lista de receitas do tipo [Recipe]
     */
    override fun getFavorites(): Flow<List<Recipe>> = callbackFlow {
        // obtém a referência para a pasta de favoritos
        val ref = getDatabaseRef()

        // cria um listener para saber se os dados mudaram
        // ValueEventListener é necessário para ler dados do Realtime Database
        val listener = object : ValueEventListener {
            // corre sempre que os dados no Firebase mudam
            override fun onDataChange(snapshot: DataSnapshot) {
                // mapeia os filhos da pasta de favoritos para uma lista de Recipe
                val favorites = snapshot.children.mapNotNull {
                    it.getValue(FavoriteRecipeFirebaseDto::class.java)?.toDomain()
                }
                // envia a lista obtida para o fluxo
                trySend(favorites)
            }
            // fecha o fluxo se ocorrer uma falha na leitura
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }

        // regista o listener na referência do Firebase
        ref.addValueEventListener(listener)

        // remove o listener quando o fluxo deixa de ser observado
        awaitClose { ref.removeEventListener(listener) }
    }

    /**
     * Adiciona uma receita aos favoritos
     *
     * @param recipe A receita a adicionar
     */
    override suspend fun addFavorite(recipe: Recipe) {
        // usa o ID da receita como chave para evitar duplicados
        getDatabaseRef().child(recipe.idMeal).setValue(recipe.toFavoriteDto()).await()
    }

    /**
     * Remove uma receita dos favoritos
     *
     * @param recipeId O identificador da receita a remover
     */
    override suspend fun removeFavorite(recipeId: String) {
        getDatabaseRef().child(recipeId).removeValue().await()
    }

    /**
     * Verifica em tempo real se uma receita está nos favoritos
     *
     * @param recipeId O identificador da receita a verificar
     * @return Um [Flow] que emite true se a receita existir nos favoritos, false caso contrário
     */
    override fun isFavorite(recipeId: String): Flow<Boolean> = callbackFlow {
        // obtém a referência da receita
        val ref = getDatabaseRef().child(recipeId)
        // cria um listener para verificar se a receita existe nos favoritos
        // https://firebase.google.com/docs/database/android/read-and-write?hl=pt-br#read_data_with_persistent_listeners
        val listener = object : ValueEventListener {
            // corre quando o estado da receita sofre alterações
            override fun onDataChange(snapshot: DataSnapshot) {
                // envia true se a receita existir nos favoritos, false caso contrário
                trySend(snapshot.exists())
            }
            // fecha o fluxo se ocorrer uma falha na leitura
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }

        // regista o listener na referência do Firebase
        ref.addValueEventListener(listener)

        // remove o listener quando o fluxo deixa de ser observado
        awaitClose { ref.removeEventListener(listener) }
    }
}

/*
 * Esta classe é a implementação do FavoriteRepository onde é utilizada a Firebase Realtime Database
 * para gerir os favoritos do utilizador
 *
 * Para obter os favoritos e verificar se uma receita está marcada, utilizei callbackFlow porque
 * é preciso converter os listeners do Firebase em fluxos, o que permite enviar atualizações
 * para a interface sempre que os dados sofrem alterações na base de dados
 * https://developer.android.com/kotlin/flow?hl=pt-br#callback
 *
 * Utiliza-se o id da receita como chave no Firebase, de modo que a mesma receita
 * não seja guardada em duplicado
 */