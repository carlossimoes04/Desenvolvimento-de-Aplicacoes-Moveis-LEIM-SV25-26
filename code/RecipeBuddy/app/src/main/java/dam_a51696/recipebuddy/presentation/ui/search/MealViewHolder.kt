package dam_a51696.recipebuddy.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dam_a51696.recipebuddy.databinding.ItemMealBinding
import dam_a51696.recipebuddy.domain.model.Meal

/**
 * ViewHolder class for displaying a single meal item in a RecyclerView.
 * 
 * This class handles the binding of [Meal] data to the item layout, including loading the 
 * meal image using Coil and setting up click listeners.
 * 
 * @property binding The view binding for the item layout.
 * @property onMealClick Callback triggered when the item is clicked.
 */
class MealViewHolder(
    private val binding: ItemMealBinding,
    private val onMealClick: (Meal) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    /**
     * Binds the provided meal data to the views.
     * 
     * @param meal The meal to display.
     */
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
        /**
         * Factory method to create a new [MealViewHolder] instance.
         * 
         * @param parent The parent ViewGroup.
         * @param onMealClick Callback for click events.
         * @return A new instance of [MealViewHolder].
         */
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
