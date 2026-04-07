package dam.a51696.picsumgallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dam.a51696.picsumgallery.model.ImageItem
import dam.a51696.picsumgallery.model.UiState
import dam.a51696.picsumgallery.repository.ImageRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ImageRepository) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<List<ImageItem>>>()
    val uiState: LiveData<UiState<List<ImageItem>>> = _uiState

    val favoritesFlow = repository.getAllFavoritesFlow()

    fun toggleFavorite(imageItem: ImageItem) {
        viewModelScope.launch {
            repository.toggleFavorite(imageItem)
        }
    }

    init {
        // Carrega as imagens iniciais assim quer o ViewModel é gerado.
        fetchImages()
    }

    fun fetchImages(page: Int = 1) {
        _uiState.value = UiState.Loading
        // O viewModelScope garante que a coroutine cancela quando a ViewModel for limpa
        viewModelScope.launch {
            val result = repository.getImages(page = page, limit = 30)
            
            result.onSuccess { imageList ->
                _uiState.value = UiState.Success(imageList)
            }.onFailure { exception ->
                _uiState.value = UiState.Error(exception.message ?: "Ocorreu um erro.")
            }
        }
    }
}

// Factory necessária porque MainViewModel tem um construtor com parâmetros.
class MainViewModelFactory(private val repository: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
