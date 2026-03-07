package dam_a51696.recipebuddy.presentation.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dam_a51696.recipebuddy.databinding.BottomSheetFilterBinding

/**
 * A bottom sheet fragment that displays a list of selectable filter options.
 * 
 * This dialog is used to let users select a specific category, area, or ingredient 
 * from a list fetched from the API.
 * 
 * @property title The title to be displayed at the top of the bottom sheet.
 * @property options The list of strings representing the available filter options.
 * @property onOptionSelected Callback function invoked when an option is selected.
 */
class FilterBottomSheet(
    private val title: String,
    private val options: List<String>,
    private val onOptionSelected: (String) -> Unit
) : BottomSheetDialogFragment() {
    
    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.filterTitle.text = title
        
        val adapter = FilterOptionAdapter { option ->
            onOptionSelected(option)
            dismiss()
        }
        
        binding.filterOptionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        
        adapter.submitList(options)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
