package dam_a51696.recipebuddy.presentation.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * RecyclerView adapter for displaying selectable filter options (categories, areas, or ingredients).
 * 
 * This adapter is used within the [FilterBottomSheet] to present a list of strings to the user.
 * 
 * @property onOptionClick Callback function invoked when an option is selected.
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
 * [DiffUtil.ItemCallback] implementation for filter option strings.
 * 
 * Since the options are simple strings, equality checks are used for both item and content identity.
 */
class FilterOptionDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
    
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
