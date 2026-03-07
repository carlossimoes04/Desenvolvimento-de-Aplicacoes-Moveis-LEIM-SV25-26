package dam_a51696.recipebuddy.presentation.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dam_a51696.recipebuddy.domain.model.Meal

/**
 * RecyclerView adapter for displaying a list of meals in the search results.
 * 
 * This adapter uses [ListAdapter] with [MealDiffCallback] for efficient list updates.
 * 
 * @property onMealClick Callback function invoked when a meal item is clicked.
 */
class SearchAdapter(
    private val onMealClick: (Meal) -> Unit
) : ListAdapter<Meal, MealViewHolder>(MealDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        return MealViewHolder.create(parent, onMealClick)
    }
    
    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = getItem(position)
        holder.bind(meal)
    }
}

/**
 * [DiffUtil.ItemCallback] implementation for [Meal] objects.
 * 
 * Used by [SearchAdapter] to calculate the difference between two lists and only update
 * the necessary items.
 */
class MealDiffCallback : DiffUtil.ItemCallback<Meal>() {
    /**
     * Checks if two items represent the same meal by comparing their IDs.
     */
    override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
        return oldItem.id == newItem.id
    }
    
    /**
     * Checks if two items have the same content by comparing the [Meal] objects.
     */
    override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
        return oldItem == newItem
    }
}
