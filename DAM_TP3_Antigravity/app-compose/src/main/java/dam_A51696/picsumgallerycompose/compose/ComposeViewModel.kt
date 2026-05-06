package dam_A51696.picsumgallerycompose.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dam_A51696.picsumgallerycompose.core.data.FavoriteEntity
import dam_A51696.picsumgallerycompose.core.model.ImageItem
import dam_A51696.picsumgallerycompose.core.model.UiState
import dam_A51696.picsumgallerycompose.core.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para a aplicação Compose.
 * Usa StateFlow (padrão reativo moderno) em vez de LiveData.
 */
class ComposeViewModel(private val repository: ImageRepository) : ViewModel() {

    // Estado principal da UI: Loading, Success ou Error
    private val _uiState = MutableStateFlow<UiState<List<ImageItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ImageItem>>> = _uiState.asStateFlow()

    // Flow de favoritos exposto como StateFlow para Compose
    val favorites: StateFlow<List<FavoriteEntity>> = repository.getAllFavoritesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        fetchImages()
    }

    fun fetchImages(page: Int = 1) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getImages(page = page, limit = 30)
            result.onSuccess { imageList ->
                _uiState.value = UiState.Success(imageList)
            }.onFailure { exception ->
                _uiState.value = UiState.Error(exception.message ?: "Ocorreu um erro.")
            }
        }
    }

    fun toggleFavorite(imageItem: ImageItem) {
        viewModelScope.launch {
            repository.toggleFavorite(imageItem)
        }
    }
}

/**
 * Factory necessária porque ComposeViewModel tem um construtor com parâmetros.
 */
class ComposeViewModelFactory(private val repository: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComposeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComposeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
