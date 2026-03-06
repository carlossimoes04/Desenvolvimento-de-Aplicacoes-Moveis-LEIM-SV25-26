package dam_a51696.recipebuddy.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam_a51696.recipebuddy.databinding.ItemFilterOptionBinding

/**
 * ViewHolder for filter option items
 */
class FilterOptionViewHolder(
    private val binding: ItemFilterOptionBinding,
    private val onOptionClick: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(option: String) {
        binding.optionText.text = option
        binding.root.setOnClickListener {
            onOptionClick(option)
        }
    }
    
    companion object {
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
