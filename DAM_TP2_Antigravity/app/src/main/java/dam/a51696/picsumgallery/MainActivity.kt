package dam.a51696.picsumgallery

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dam.a51696.picsumgallery.adapter.ImageAdapter
import dam.a51696.picsumgallery.api.PicsumApiService
import dam.a51696.picsumgallery.data.AppDatabase
import dam.a51696.picsumgallery.data.FavoriteEntity
import dam.a51696.picsumgallery.databinding.ActivityMainBinding
import dam.a51696.picsumgallery.model.ImageItem
import dam.a51696.picsumgallery.model.UiState
import dam.a51696.picsumgallery.repository.ImageRepository
import dam.a51696.picsumgallery.viewmodel.MainViewModel
import dam.a51696.picsumgallery.viewmodel.MainViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ImageAdapter

    // Inicialização manual da Stack de Dados (Retrofit -> Api -> Repo -> Factory)
    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://picsum.photos/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicsumApiService::class.java)
    }

    private val database by lazy { dam.a51696.picsumgallery.data.AppDatabase.getDatabase(applicationContext) }

    private val repository by lazy { ImageRepository(apiService, database.favoriteDao(), database.cacheDao()) }

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Povoa layout pela ViewBinding para navegação segura contra nulls
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Toolbar top bar
        setSupportActionBar(binding.toolbar)

        // 2. Ligar RecyclerView ao Adapter
        adapter = ImageAdapter(
            onFavoriteClick = { item -> viewModel.toggleFavorite(item) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 3. Atualizar (Swipe) fará refetch silencioso de página 1
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchImages(1)
        }

        // 4. Observar UiState Centralizado!
        viewModel.uiState.observe(this) { state: UiState<List<ImageItem>> ->
            when (state) {
                is UiState.Loading -> {
                    if (adapter.currentList.isEmpty() && !binding.swipeRefreshLayout.isRefreshing) {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
                is UiState.Success<*> -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    @Suppress("UNCHECKED_CAST")
                    adapter.submitList(state.data as List<ImageItem>) 
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    Snackbar.make(
                        binding.root,
                        "Erro: ${state.message}",
                        Snackbar.LENGTH_LONG
                    ).setAction("Tentar Novamente") { viewModel.fetchImages(1) }.show()
                }
            }
        }
        
        // 5. Global Favorites Floating UI Logic
        var currentFavorites: List<FavoriteEntity> = emptyList()
        viewModel.favoritesFlow.asLiveData().observe(this) { favs: List<FavoriteEntity> ->
            currentFavorites = favs
        }

        binding.fabFavorites.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_favorites, null)
            bottomSheet.setContentView(dialogView)

            val recyclerFav = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerFavorites)
            val dummyFavAdapter = ImageAdapter { /* no-op */ }
            recyclerFav.layoutManager = LinearLayoutManager(this)
            recyclerFav.adapter = dummyFavAdapter
            
            dummyFavAdapter.submitList(currentFavorites.map { ImageItem(it.id, it.author, it.downloadUrl) })
            bottomSheet.show()
        }
        
        // 6. Resgatar!
        viewModel.fetchImages(1)
    }
}
