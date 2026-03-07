package dam_a51696.recipebuddy.presentation.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dam_a51696.recipebuddy.R
import dam_a51696.recipebuddy.databinding.FragmentSearchBinding
import dam_a51696.recipebuddy.presentation.state.SearchUiState
import dam_a51696.recipebuddy.presentation.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment that provides meal search and filtering UI.
 * 
 * Users can search for meals by name using a search bar or filter them by category,
 * area, or ingredient using chips that open a bottom sheet.
 */
@AndroidEntryPoint
class SearchFragment : Fragment() {
    
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: SearchAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearchInput()
        setupFilterChips()
        setupClearFilterChip()
        observeUiState()
        observeFilterStates()
        observeActiveFilters()
    }
    
    /**
     * Initializes the [SearchAdapter] and attaches it to the RecyclerView.
     */
    private fun setupRecyclerView() {
        adapter = SearchAdapter { meal ->
            val action = SearchFragmentDirections
                .actionSearchFragmentToDetailFragment(meal.id)
            findNavController().navigate(action)
        }
        
        binding.mealsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SearchFragment.adapter
        }
    }
    
    /**
     * Sets up the text change listener for the search input field.
     */
    private fun setupSearchInput() {
        binding.searchInput.addTextChangedListener { text ->
            if (!text.isNullOrBlank()) {
                // Clear filters when searching by name
                viewModel.clearFilters()
            }
            viewModel.searchMeals(text?.toString() ?: "")
        }
    }
    
    /**
     * Configures the click listeners for the filter chips.
     */
    private fun setupFilterChips() {
        binding.categoryFilterChip.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val categories = viewModel.categories.value
                if (categories.isNotEmpty()) {
                    showFilterBottomSheet(
                        getString(R.string.select_category),
                        categories
                    ) { category ->
                        // Clear search input
                        binding.searchInput.text?.clear()
                        viewModel.filterByCategory(category)
                    }
                }
            }
        }
        
        binding.areaFilterChip.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val areas = viewModel.areas.value
                if (areas.isNotEmpty()) {
                    showFilterBottomSheet(
                        getString(R.string.select_area),
                        areas
                    ) { area ->
                        // Clear search input
                        binding.searchInput.text?.clear()
                        viewModel.filterByArea(area)
                    }
                }
            }
        }
        
        binding.ingredientFilterChip.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val ingredients = viewModel.ingredients.value
                if (ingredients.isNotEmpty()) {
                    showFilterBottomSheet(
                        getString(R.string.select_ingredient),
                        ingredients
                    ) { ingredient ->
                        // Clear search input
                        binding.searchInput.text?.clear()
                        viewModel.filterByIngredient(ingredient)
                    }
                }
            }
        }
    }
    
    /**
     * Sets up the listener for the 'Clear Filters' chip.
     */
    private fun setupClearFilterChip() {
        binding.clearFiltersChip.setOnClickListener {
            // Clear search input
            binding.searchInput.text?.clear()
            viewModel.clearFilters()
        }
    }
    
    /**
     * Displays a [FilterBottomSheet] with the given title and options.
     * 
     * @param title The title to show at the top of the bottom sheet.
     * @param options The list of strings to display as selectable options.
     * @param onOptionSelected Callback triggered when an option is selected.
     */
    private fun showFilterBottomSheet(
        title: String,
        options: List<String>,
        onOptionSelected: (String) -> Unit
    ) {
        FilterBottomSheet(title, options, onOptionSelected)
            .show(childFragmentManager, "FilterBottomSheet")
    }
    
    /**
     * Observes the selected filter states from the ViewModel and updates chip UI.
     */
    private fun observeFilterStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedCategory.collect { category ->
                binding.categoryFilterChip.isChecked = category != null
                if (category != null) {
                    binding.categoryFilterChip.text = category
                } else {
                    binding.categoryFilterChip.text = getString(R.string.filter_category)
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedArea.collect { area ->
                binding.areaFilterChip.isChecked = area != null
                if (area != null) {
                    binding.areaFilterChip.text = area
                } else {
                    binding.areaFilterChip.text = getString(R.string.filter_area)
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedIngredient.collect { ingredient ->
                binding.ingredientFilterChip.isChecked = ingredient != null
                if (ingredient != null) {
                    binding.ingredientFilterChip.text = ingredient
                } else {
                    binding.ingredientFilterChip.text = getString(R.string.filter_ingredient)
                }
            }
        }
    }
    
    /**
     * Observes whether any filters are active to enable/disable the clear chip.
     */
    private fun observeActiveFilters() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hasActiveFilters.collect { hasFilters ->
                // Enable/disable the clear filters chip based on active filters
                binding.clearFiltersChip.isEnabled = hasFilters
            }
        }
    }
    
    /**
     * Observes the main [SearchUiState] and updates the visibility of UI components.
     */
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    SearchUiState.Idle -> {
                        binding.mealsRecyclerView.visibility = View.GONE
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.errorContainer.visibility = View.GONE
                        binding.emptyStateText.visibility = View.GONE
                    }
                    SearchUiState.Loading -> {
                        binding.mealsRecyclerView.visibility = View.GONE
                        binding.loadingProgressBar.visibility = View.VISIBLE
                        binding.errorContainer.visibility = View.GONE
                        binding.emptyStateText.visibility = View.GONE
                    }
                    is SearchUiState.Success -> {
                        binding.mealsRecyclerView.visibility = View.VISIBLE
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.errorContainer.visibility = View.GONE
                        binding.emptyStateText.visibility = View.GONE
                        adapter.submitList(state.meals)
                    }
                    is SearchUiState.Error -> {
                        binding.mealsRecyclerView.visibility = View.GONE
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.errorContainer.visibility = View.VISIBLE
                        binding.emptyStateText.visibility = View.GONE
                        binding.errorMessage.text = state.message
                    }
                    SearchUiState.NoResults -> {
                        binding.mealsRecyclerView.visibility = View.GONE
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.errorContainer.visibility = View.GONE
                        binding.emptyStateText.visibility = View.VISIBLE
                        binding.emptyStateText.text = getString(R.string.no_meals_found)
                    }
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
