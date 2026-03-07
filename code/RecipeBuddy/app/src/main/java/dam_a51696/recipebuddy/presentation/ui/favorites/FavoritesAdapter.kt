package dam_a51696.recipebuddy.presentation.ui.favorites

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dam_a51696.recipebuddy.domain.model.MealDetail

/**
 * RecyclerView adapter for displaying the list of favorite meals.
 * 
 * This adapter uses [ListAdapter] with [FavoriteDiffCallback] for efficient list updates.
 * 
 * @property onMealClick Callback function invoked when a favorite meal item is clicked.
 * @property onRemoveClick Callback function invoked when the remove button on a meal item is clicked.
 */
class FavoritesAdapter(
    private val onMealClick: (MealDetail) -> Unit,
    private val onRemoveClick: (MealDetail) -> Unit
) : ListAdapter<MealDetail, FavoriteViewHolder>(FavoriteDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder.create(parent, onMealClick, onRemoveClick)
    }
    
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val meal = getItem(position)
        holder.bind(meal)
    }
}

/**
 * [DiffUtil.ItemCallback] implementation for [MealDetail] objects in the favorites list.
 */
class FavoriteDiffCallback : DiffUtil.ItemCallback<MealDetail>() {
    /**
     * Checks if two items represent the same meal by comparing their IDs.
     */
    override fun areItemsTheSame(oldItem: MealDetail, newItem: MealDetail): Boolean {
        return oldItem.id == newItem.id
    }
    
    /**
     * Checks if two items have the same content.
     */
    override fun areContentsTheSame(oldItem: MealDetail, newItem: MealDetail): Boolean {
        return oldItem == newItem
    }
}
