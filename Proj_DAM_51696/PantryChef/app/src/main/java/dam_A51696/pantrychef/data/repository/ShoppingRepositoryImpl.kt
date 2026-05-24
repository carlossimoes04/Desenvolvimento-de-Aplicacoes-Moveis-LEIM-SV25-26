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
import javax.inject.Inject

class ShoppingRepositoryImpl @Inject constructor() : ShoppingRepository {

    private fun getDatabaseRef(): DatabaseReference {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unauthenticated"
        return FirebaseDatabase.getInstance().getReference("users/$userId/shopping_list")
    }

    override fun getAllItems(): Flow<List<ShoppingItem>> = callbackFlow {
        val ref = getDatabaseRef()
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull {
                    it.getValue(ShoppingItemFirebaseDto::class.java)?.toDomain()
                }
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun addItem(item: ShoppingItem) {
        val ref = getDatabaseRef()
        val newRef = ref.push()
        val itemDto = item.toDto().copy(id = newRef.key ?: "")
        newRef.setValue(itemDto)
    }

    override suspend fun toggleItem(item: ShoppingItem) {
        val updatedDto = item.copy(isBought = !item.isBought).toDto()
        getDatabaseRef().child(item.id).setValue(updatedDto)
    }

    override suspend fun deleteItem(item: ShoppingItem) {
        getDatabaseRef().child(item.id).removeValue()
    }
}
