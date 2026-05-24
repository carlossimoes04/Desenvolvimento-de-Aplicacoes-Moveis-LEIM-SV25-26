package dam_A51696.pantrychef.presentation.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.model.Ingredient
import dam_A51696.pantrychef.domain.repository.PantryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PantryViewModel @Inject constructor(
    private val pantryRepository: PantryRepository
) : ViewModel() {

    val ingredients: StateFlow<List<Ingredient>> = pantryRepository.getAllIngredients()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addIngredient(name: String, quantity: Double, unit: String, expirationDate: Long) {
        viewModelScope.launch {
            val newIngredient = Ingredient(
                id = "",
                name = name,
                quantity = quantity,
                unit = unit,
                expirationDate = expirationDate
            )
            pantryRepository.addIngredient(newIngredient)
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            pantryRepository.updateIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            pantryRepository.deleteIngredient(ingredient)
        }
    }
}
