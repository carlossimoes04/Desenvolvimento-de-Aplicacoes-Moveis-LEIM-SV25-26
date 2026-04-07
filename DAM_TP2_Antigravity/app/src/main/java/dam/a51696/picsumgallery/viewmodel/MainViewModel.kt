package dam.a51696.picsumgallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dam.a51696.picsumgallery.model.ImageItem
import dam.a51696.picsumgallery.repository.ImageRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ImageRepository) : ViewModel() {

    private val _images = MutableLiveData<List<ImageItem>>()
    val images: LiveData<List<ImageItem>> = _images

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        // Carrega as imagens iniciais assim quer o ViewModel é gerado.
        fetchImages()
    }

    fun fetchImages(page: Int = 1) {
        _isLoading.value = true
        // O viewModelScope garante que a coroutine cancela quando a ViewModel for limpa
        viewModelScope.launch {
            val result = repository.getImages(page = page, limit = 30)
            
            result.onSuccess { imageList ->
                _images.value = imageList
            }.onFailure {
                // Para uma APP Real podíamos meter um LiveData de ErrorEvent para invocar toasts
                // ou atualizar uma UI de erro. 
                // Por agora não quebramos a app, mas poderemos futuramente avisar o utilizador.
            }
            
            _isLoading.value = false
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
