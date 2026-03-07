package dam_a51696.recipebuddy.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam_a51696.recipebuddy.databinding.ItemFilterOptionBinding

/**
 * ViewHolder class for displaying a single filter option string in a RecyclerView.
 * 
 * @property binding The view binding for the item layout.
 * @property onOptionClick Callback triggered when the option is clicked.
 */
class FilterOptionViewHolder(
    private val binding: ItemFilterOptionBinding,
    private val onOptionClick: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    /**
     * Binds the filter option text to the view and sets the click listener.
     * 
     * @param option The filter option string to display.
     */
    fun bind(option: String) {
        binding.optionText.text = option
        binding.root.setOnClickListener {
            onOptionClick(option)
        }
    }
    
    companion object {
        /**
         * Factory method to create a new [FilterOptionViewHolder] instance.
         * 
         * @param parent The parent ViewGroup.
         * @param onOptionClick Callback for click events.
         * @return A new instance of [FilterOptionViewHolder].
         */
        fun create(parent: ViewGroup, onOptionClick: (String) -> Unit): FilterOptionViewHolder {
            val binding = ItemFilterOptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return FilterOptionViewHolder(binding, onOptionClick)
        }
    }
}
