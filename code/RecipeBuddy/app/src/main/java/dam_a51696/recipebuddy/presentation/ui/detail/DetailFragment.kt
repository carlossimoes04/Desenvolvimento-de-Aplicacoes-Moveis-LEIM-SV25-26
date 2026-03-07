package dam_a51696.recipebuddy.presentation.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import dam_a51696.recipebuddy.R
import dam_a51696.recipebuddy.databinding.FragmentDetailBinding
import dam_a51696.recipebuddy.presentation.state.DetailUiState
import dam_a51696.recipebuddy.presentation.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment that displays detailed information about a selected meal.
 * 
 * It shows the meal's image, name, instructions, and ingredients.
 * It also allows users to toggle the meal's favorite status using a Floating Action Button.
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {
    
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFavoriteButton()
        observeUiState()
        observeFavoriteState()
    }
    
    /**
     * Sets up the click listener for the favorite Floating Action Button.
     */
    private fun setupFavoriteButton() {
        binding.favoriteFab.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }
    
    /**
     * Observes the main [DetailUiState] from the ViewModel and updates the UI accordingly.
     */
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    DetailUiState.Loading -> {
                        showLoading()
                    }
                    is DetailUiState.Success -> {
                        showMealDetail(state)
                    }
                    is DetailUiState.Error -> {
                        showError(state.message)
                    }
                }
            }
        }
    }
    
    /**
     * Observes the favorite status of the current meal and updates the FAB icon.
     */
    private fun observeFavoriteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                updateFavoriteIcon(isFavorite)
            }
        }
    }
    
    /**
     * Updates the icon of the favorite Floating Action Button based on favorited state.
     * 
     * @param isFavorite True if the meal is a favorite, false otherwise.
     */
    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.favoriteFab.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        )
    }
    
    /**
     * Displays the loading progress bar and hides other UI elements.
     */
    private fun showLoading() {
        binding.apply {
            loadingProgressBar.visibility = View.VISIBLE
            mealContent.visibility = View.GONE
            errorContainer.visibility = View.GONE
            favoriteFab.visibility = View.GONE
        }
    }
    
    /**
     * Displays the meal details on the screen.
     * 
     * @param state The successful UI state containing the meal data.
     */
    private fun showMealDetail(state: DetailUiState.Success) {
        val meal = state.meal
        binding.apply {
            loadingProgressBar.visibility = View.GONE
            mealContent.visibility = View.VISIBLE
            errorContainer.visibility = View.GONE
            favoriteFab.visibility = View.VISIBLE
            
            mealImage.load(meal.imageUrl) {
                crossfade(true)
            }
            
            mealTitle.text = meal.name
            
            mealInstructions.text = meal.instructions
            
            // Build ingredients text
            val ingredientsText = meal.ingredients.joinToString("\n") { (name, measure) ->
                "• $measure $name"
            }
            ingredientsText.also { mealIngredients.text = it }
        }
    }
    
    /**
     * Displays an error message and a retry button.
     * 
     * @param message The error message to be displayed.
     */
    private fun showError(message: String) {
        binding.apply {
            loadingProgressBar.visibility = View.GONE
            mealContent.visibility = View.GONE
            errorContainer.visibility = View.VISIBLE
            favoriteFab.visibility = View.GONE
            errorMessage.text = message
            
            retryButton.setOnClickListener {
                viewModel.retry(args.mealId)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
