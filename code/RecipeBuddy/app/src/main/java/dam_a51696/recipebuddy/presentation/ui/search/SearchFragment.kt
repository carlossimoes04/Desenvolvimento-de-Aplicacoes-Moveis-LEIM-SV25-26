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
 * Fragment for meal search functionality
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
    
    private fun setupSearchInput() {
        binding.searchInput.addTextChangedListener { text ->
            if (!text.isNullOrBlank()) {
                // Clear filters when searching by name
                viewModel.clearFilters()
            }
            viewModel.searchMeals(text?.toString() ?: "")
        }
    }
    
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
    
    private fun setupClearFilterChip() {
        binding.clearFiltersChip.setOnClickListener {
            // Clear search input
            binding.searchInput.text?.clear()
            viewModel.clearFilters()
        }
    }
    
    private fun showFilterBottomSheet(
        title: String,
        options: List<String>,
        onOptionSelected: (String) -> Unit
    ) {
        FilterBottomSheet(title, options, onOptionSelected)
            .show(childFragmentManager, "FilterBottomSheet")
    }
    
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
    
    private fun observeActiveFilters() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hasActiveFilters.collect { hasFilters ->
                // Enable/disable the clear filters chip based on active filters
                binding.clearFiltersChip.isEnabled = hasFilters
            }
        }
    }
    
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
