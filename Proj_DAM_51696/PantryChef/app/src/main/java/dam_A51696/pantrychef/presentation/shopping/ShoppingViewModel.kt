package dam_A51696.pantrychef.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.model.ShoppingItem
import dam_A51696.pantrychef.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    val items: StateFlow<List<ShoppingItem>> = shoppingRepository.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addItem(name: String, details: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            shoppingRepository.addItem(
                ShoppingItem(id = "", name = name, details = details, isBought = false)
            )
        }
    }

    fun toggleItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.toggleItem(item)
        }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.deleteItem(item)
        }
    }
}
