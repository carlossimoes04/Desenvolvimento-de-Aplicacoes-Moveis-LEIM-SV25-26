package dam.a51696.picsumgallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dam.a51696.picsumgallery.adapter.ImageAdapter
import dam.a51696.picsumgallery.api.PicsumApiService
import dam.a51696.picsumgallery.databinding.ActivityMainBinding
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

    private val repository by lazy { ImageRepository(apiService) }

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
        adapter = ImageAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 3. Atualizar (Swipe) fará refetch silencioso de página 1
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchImages(1)
        }

        // 4. Observar Mudanças na Lista!
        viewModel.images.observe(this) { imagesList ->
            adapter.submitList(imagesList) // magia do DiffUtil fará animação e recontagem
        }

        // 5. Observar Global Loading state
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                // Mostrar a ProgressBar apanes se for o loading inicial (lista vazia e sem swipe a decorrer)
                if (adapter.currentList.isEmpty() && !binding.swipeRefreshLayout.isRefreshing) {
                    binding.progressBar.visibility = View.VISIBLE
                }
            } else {
                // Quando o loading esgota (independentemente de sucesso ou erro)
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
        
        // 6. Resgatar!
        viewModel.fetchImages(1)
    }
}
