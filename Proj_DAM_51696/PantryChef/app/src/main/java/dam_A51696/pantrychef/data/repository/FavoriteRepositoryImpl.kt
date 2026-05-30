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
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor() : FavoriteRepository {

    // guarda na pasta "favorites" dentro do UID do utilizador
    private fun getDatabaseRef(): DatabaseReference {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unauthenticated"
        return FirebaseDatabase.getInstance().getReference("users/$userId/favorites")
    }

    override fun getFavorites(): Flow<List<Recipe>> = callbackFlow {
        val ref = getDatabaseRef()
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // lê as receitas do Firebase
                val favorites = snapshot.children.mapNotNull {
                    it.getValue(FavoriteRecipeFirebaseDto::class.java)?.toDomain()
                }
                trySend(favorites)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun addFavorite(recipe: Recipe) {
        // usa o ID da receita como chave para evitar duplicados
        getDatabaseRef().child(recipe.idMeal).setValue(recipe.toFavoriteDto()).await()
    }

    override suspend fun removeFavorite(recipeId: String) {
        getDatabaseRef().child(recipeId).removeValue().await()
    }

    override fun isFavorite(recipeId: String): Flow<Boolean> = callbackFlow {
        val ref = getDatabaseRef().child(recipeId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.exists()) // retorna True se a receita existir na Base de Dados
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}