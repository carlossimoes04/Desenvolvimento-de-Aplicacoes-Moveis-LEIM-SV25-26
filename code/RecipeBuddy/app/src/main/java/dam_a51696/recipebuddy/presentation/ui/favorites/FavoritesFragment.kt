package dam_a51696.recipebuddy.presentation.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dam_a51696.recipebuddy.databinding.FragmentFavoritesBinding
import dam_a51696.recipebuddy.presentation.state.FavoritesUiState
import dam_a51696.recipebuddy.presentation.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment that displays the user's list of favorite meals.
 * 
 * This fragment uses a RecyclerView to show the favorited recipes and allows the user
 * to view their details or remove them from the favorites list.
 */
@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: FavoritesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeUiState()
    }
    
    /**
     * Initializes the [FavoritesAdapter] and attaches it to the RecyclerView.
     */
    private fun setupRecyclerView() {
        adapter = FavoritesAdapter(
            onMealClick = { meal ->
                val action = FavoritesFragmentDirections
                    .actionFavoritesFragmentToDetailFragment(meal.id)
                findNavController().navigate(action)
            },
            onRemoveClick = { meal ->
                viewModel.removeFromFavorites(meal.id)
            }
        )
        
        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritesFragment.adapter
        }
    }
    
    /**
     * Observes the [FavoritesUiState] from the ViewModel and updates the UI visibility.
     */
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    FavoritesUiState.Empty -> {
                        binding.favoritesRecyclerView.visibility = View.GONE
                        binding.emptyStateText.visibility = View.VISIBLE
                    }
                    is FavoritesUiState.Success -> {
                        binding.favoritesRecyclerView.visibility = View.VISIBLE
                        binding.emptyStateText.visibility = View.GONE
                        adapter.submitList(state.favorites)
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
