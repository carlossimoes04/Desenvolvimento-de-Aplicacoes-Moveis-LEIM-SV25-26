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
        observeUiState()
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
            viewModel.searchMeals(text?.toString() ?: "")
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
                        binding.emptyStateText.text = "No meals found. Try another search."
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
