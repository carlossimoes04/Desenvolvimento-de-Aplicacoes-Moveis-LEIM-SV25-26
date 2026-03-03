package dam_a51696.recipebuddy.presentation.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dam_a51696.recipebuddy.domain.model.Meal

/**
 * RecyclerView adapter for displaying meal search results
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
 * DiffUtil callback for efficient RecyclerView updates
 */
class MealDiffCallback : DiffUtil.ItemCallback<Meal>() {
    override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
        return oldItem == newItem
    }
}
