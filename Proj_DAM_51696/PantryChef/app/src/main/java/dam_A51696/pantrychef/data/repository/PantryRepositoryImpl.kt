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
import javax.inject.Inject

class PantryRepositoryImpl @Inject constructor() : PantryRepository {

    private fun getDatabaseRef(): DatabaseReference {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unauthenticated"
        return FirebaseDatabase.getInstance().getReference("users/$userId/pantry")
    }

    override suspend fun addIngredient(ingredient: Ingredient) {
        val ref = getDatabaseRef()
        val newRef = ref.push() // Gera automaticamente uma chave alfanumérica única
        
        // Converte-se o domínio para DTO, injetando o novo ID que o Firebase gerou
        val ingredientDto = ingredient.toDto().copy(id = newRef.key ?: "")
        
        // Guarda na nuvem
        newRef.setValue(ingredientDto) 
    }

    // Retorna um Flow contínuo (um canal que envia dados automaticamente sempre que o Firebase mudar)
    override fun getAllIngredients(): Flow<List<Ingredient>> = callbackFlow {
        val ref = getDatabaseRef()
        // Pede-se ao Firebase para enviar os dados ordenados pela data de expiração (crescente)
        val query = ref.orderByChild("expirationDate")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Para cada nó (ingrediente) no Firebase, converte do JSON para IngredientFirebaseDto, 
                // e logo a seguir converte para o modelo de Domínio (toDomain)
                val ingredients = snapshot.children.mapNotNull { 
                    it.getValue(IngredientFirebaseDto::class.java)?.toDomain() 
                }
                
                // Emite a lista pronta para quem estiver a "ouvir" (ex: o ViewModel)
                trySend(ingredients) 
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException()) // Fecha o canal em caso de erro de leitura
            }
        }

        query.addValueEventListener(listener)
        
        // Quando o Ecrã for fechado e já não precisar dos dados, 
        // desconecta-se do Firebase para não gastar bateria/dados da net
        awaitClose { query.removeEventListener(listener) }
    }

    override suspend fun deleteIngredient(ingredient: Ingredient) {
        getDatabaseRef().child(ingredient.id).removeValue()
    }

    override suspend fun updateIngredient(ingredient: Ingredient) {
        getDatabaseRef().child(ingredient.id).setValue(ingredient.toDto())
    }
}

/**
 * Foi adicionada a anotação @Inject constructor() no repositório. 
 * Isto avisa o Hilt de como ele deve construir este repositório caso algum ViewModel precise dele.
 */