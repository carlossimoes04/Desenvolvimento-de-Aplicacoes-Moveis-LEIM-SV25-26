package dam_a51696.recipebuddy.presentation.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dam_a51696.recipebuddy.databinding.ItemFavoriteMealBinding
import dam_a51696.recipebuddy.domain.model.MealDetail

/**
 * ViewHolder class for displaying a single favorite meal item in a RecyclerView.
 * 
 * This class handles the binding of [MealDetail] data to the layout, including 
 * displaying the meal name, loading the thumbnail image, and setting up clicks 
 * for viewing details or removing from favorites.
 * 
 * @property binding The view binding for the item layout.
 * @property onMealClick Callback triggered when the item is clicked.
 * @property onRemoveClick Callback triggered when the remove button is clicked.
 */
class FavoriteViewHolder(
    private val binding: ItemFavoriteMealBinding,
    private val onMealClick: (MealDetail) -> Unit,
    private val onRemoveClick: (MealDetail) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    /**
     * Binds the provided meal details to the views.
     * 
     * @param meal The meal details to display.
     */
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
        /**
         * Factory method to create a new [FavoriteViewHolder] instance.
         * 
         * @param parent The parent ViewGroup.
         * @param onMealClick Callback for click events.
         * @param onRemoveClick Callback for remove events.
         * @return A new instance of [FavoriteViewHolder].
         */
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
