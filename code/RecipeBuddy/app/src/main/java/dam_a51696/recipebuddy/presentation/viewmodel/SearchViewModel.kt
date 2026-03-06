package dam_a51696.recipebuddy.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_a51696.recipebuddy.domain.usecase.SearchMealsUseCase
import dam_a51696.recipebuddy.domain.usecase.FilterByCategoryUseCase
import dam_a51696.recipebuddy.domain.usecase.FilterByAreaUseCase
import dam_a51696.recipebuddy.domain.usecase.FilterByIngredientUseCase
import dam_a51696.recipebuddy.domain.usecase.GetAllCategoriesUseCase
import dam_a51696.recipebuddy.domain.usecase.GetAllAreasUseCase
import dam_a51696.recipebuddy.domain.usecase.GetAllIngredientsUseCase
import dam_a51696.recipebuddy.presentation.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Search screen
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMealsUseCase: SearchMealsUseCase,
    private val filterByCategoryUseCase: FilterByCategoryUseCase,
    private val filterByAreaUseCase: FilterByAreaUseCase,
    private val filterByIngredientUseCase: FilterByIngredientUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getAllAreasUseCase: GetAllAreasUseCase,
    private val getAllIngredientsUseCase: GetAllIngredientsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState
    
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories
    
    private val _areas = MutableStateFlow<List<String>>(emptyList())
    val areas: StateFlow<List<String>> = _areas
    
    private val _ingredients = MutableStateFlow<List<String>>(emptyList())
    val ingredients: StateFlow<List<String>> = _ingredients
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory
    
    private val _selectedArea = MutableStateFlow<String?>(null)
    val selectedArea: StateFlow<String?> = _selectedArea
    
    private val _selectedIngredient = MutableStateFlow<String?>(null)
    val selectedIngredient: StateFlow<String?> = _selectedIngredient
    
    private val _hasActiveFilters = MutableStateFlow(false)
    val hasActiveFilters: StateFlow<Boolean> = _hasActiveFilters
    
    init {
        loadFilterOptions()
    }
    
    private fun loadFilterOptions() {
        viewModelScope.launch {
            // Load categories
            getAllCategoriesUseCase().onSuccess { categoryList ->
                _categories.value = categoryList
            }
            
            // Load areas
            getAllAreasUseCase().onSuccess { areaList ->
                _areas.value = areaList
            }
            
            // Load ingredients
            getAllIngredientsUseCase().onSuccess { ingredientList ->
                _ingredients.value = ingredientList
            }
        }
    }
    
    fun searchMeals(query: String) {
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }
        
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            
            val result = searchMealsUseCase(query)
            
            result.onSuccess { meals ->
                if (meals.isEmpty()) {
                    _uiState.value = SearchUiState.NoResults
                } else {
                    _uiState.value = SearchUiState.Success(meals)
                }
            }.onFailure { throwable ->
                _uiState.value = SearchUiState.Error(
                    throwable.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun filterByCategory(category: String) {
        _selectedCategory.value = category
        _selectedArea.value = null
        _selectedIngredient.value = null
        _hasActiveFilters.value = true
        
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            
            val result = filterByCategoryUseCase(category)
            
            result.onSuccess { meals ->
                if (meals.isEmpty()) {
                    _uiState.value = SearchUiState.NoResults
                } else {
                    _uiState.value = SearchUiState.Success(meals)
                }
            }.onFailure { throwable ->
                _uiState.value = SearchUiState.Error(
                    throwable.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun filterByArea(area: String) {
        _selectedCategory.value = null
        _selectedArea.value = area
        _selectedIngredient.value = null
        _hasActiveFilters.value = true
        
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            
            val result = filterByAreaUseCase(area)
            
            result.onSuccess { meals ->
                if (meals.isEmpty()) {
                    _uiState.value = SearchUiState.NoResults
                } else {
                    _uiState.value = SearchUiState.Success(meals)
                }
            }.onFailure { throwable ->
                _uiState.value = SearchUiState.Error(
                    throwable.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun filterByIngredient(ingredient: String) {
        _selectedCategory.value = null
        _selectedArea.value = null
        _selectedIngredient.value = ingredient
        _hasActiveFilters.value = true
        
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            
            val result = filterByIngredientUseCase(ingredient)
            
            result.onSuccess { meals ->
                if (meals.isEmpty()) {
                    _uiState.value = SearchUiState.NoResults
                } else {
                    _uiState.value = SearchUiState.Success(meals)
                }
            }.onFailure { throwable ->
                _uiState.value = SearchUiState.Error(
                    throwable.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun clearFilters() {
        _selectedCategory.value = null
        _selectedArea.value = null
        _selectedIngredient.value = null
        _hasActiveFilters.value = false
        _uiState.value = SearchUiState.Idle
    }
}
