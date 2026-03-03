package dam_a51696.recipebuddy.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dam_a51696.recipebuddy.databinding.ItemMealBinding
import dam_a51696.recipebuddy.domain.model.Meal

/**
 * ViewHolder for meal items in RecyclerView
 */
class MealViewHolder(
    private val binding: ItemMealBinding,
    private val onMealClick: (Meal) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(meal: Meal) {
        binding.apply {
            mealName.text = meal.name
            mealImage.load(meal.imageUrl) {
                crossfade(true)
            }
            root.setOnClickListener {
                onMealClick(meal)
            }
        }
    }
    
    companion object {
        fun create(parent: ViewGroup, onMealClick: (Meal) -> Unit): MealViewHolder {
            val binding = ItemMealBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return MealViewHolder(binding, onMealClick)
        }
    }
}
