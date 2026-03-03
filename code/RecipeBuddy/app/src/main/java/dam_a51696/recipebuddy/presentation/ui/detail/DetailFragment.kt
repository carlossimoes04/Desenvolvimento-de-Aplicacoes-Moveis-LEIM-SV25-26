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
import dam_a51696.recipebuddy.databinding.FragmentDetailBinding
import dam_a51696.recipebuddy.presentation.state.DetailUiState
import dam_a51696.recipebuddy.presentation.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment for displaying meal details
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
        observeUiState()
    }
    
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
    
    private fun showLoading() {
        binding.apply {
            loadingProgressBar.visibility = View.VISIBLE
            mealContent.visibility = View.GONE
            errorContainer.visibility = View.GONE
        }
    }
    
    private fun showMealDetail(state: DetailUiState.Success) {
        val meal = state.meal
        binding.apply {
            loadingProgressBar.visibility = View.GONE
            mealContent.visibility = View.VISIBLE
            errorContainer.visibility = View.GONE
            
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
    
    private fun showError(message: String) {
        binding.apply {
            loadingProgressBar.visibility = View.GONE
            mealContent.visibility = View.GONE
            errorContainer.visibility = View.VISIBLE
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
