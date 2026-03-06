package dam_a51696.recipebuddy.presentation.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * RecyclerView adapter for filter options
 */
class FilterOptionAdapter(
    private val onOptionClick: (String) -> Unit
) : ListAdapter<String, FilterOptionViewHolder>(FilterOptionDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterOptionViewHolder {
        return FilterOptionViewHolder.create(parent, onOptionClick)
    }
    
    override fun onBindViewHolder(holder: FilterOptionViewHolder, position: Int) {
        val option = getItem(position)
        holder.bind(option)
    }
}

/**
 * DiffUtil callback for filter options
 */
class FilterOptionDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
    
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
