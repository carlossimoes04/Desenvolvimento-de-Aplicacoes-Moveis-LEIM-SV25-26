package dam_a51696.recipebuddy.presentation.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dam_a51696.recipebuddy.databinding.ItemFavoriteMealBinding
import dam_a51696.recipebuddy.domain.model.MealDetail

/**
 * ViewHolder for favorite meal items in RecyclerView
 */
class FavoriteViewHolder(
    private val binding: ItemFavoriteMealBinding,
    private val onMealClick: (MealDetail) -> Unit,
    private val onRemoveClick: (MealDetail) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(meal: MealDetail) {
        binding.apply {
            mealName.text = meal.name
            mealImage.load(meal.imageUrl) {
                crossfade(true)
            }
            root.setOnClickListener {
                onMealClick(meal)
            }
            removeFavoriteButton.setOnClickListener {
                onRemoveClick(meal)
            }
        }
    }
    
    companion object {
        fun create(
            parent: ViewGroup,
            onMealClick: (MealDetail) -> Unit,
            onRemoveClick: (MealDetail) -> Unit
        ): FavoriteViewHolder {
            val binding = ItemFavoriteMealBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return FavoriteViewHolder(binding, onMealClick, onRemoveClick)
        }
    }
}
