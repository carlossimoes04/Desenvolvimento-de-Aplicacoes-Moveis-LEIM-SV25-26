package dam_a51696.recipebuddy.presentation.ui.favorites

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dam_a51696.recipebuddy.domain.model.MealDetail

/**
 * RecyclerView adapter for displaying favorite meals
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
 * DiffUtil callback for efficient RecyclerView updates
 */
class FavoriteDiffCallback : DiffUtil.ItemCallback<MealDetail>() {
    override fun areItemsTheSame(oldItem: MealDetail, newItem: MealDetail): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: MealDetail, newItem: MealDetail): Boolean {
        return oldItem == newItem
    }
}
