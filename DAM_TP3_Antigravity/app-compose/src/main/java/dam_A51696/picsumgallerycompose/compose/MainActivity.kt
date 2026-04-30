package dam_A51696.picsumgallerycompose.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import dam_A51696.picsumgallerycompose.core.data.AppDatabase
import dam_A51696.picsumgallerycompose.core.network.PicsumApiService
import dam_A51696.picsumgallerycompose.core.repository.ImageRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    // Inicialização manual da Stack de Dados (Retrofit -> Api -> Repo)
    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://picsum.photos/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicsumApiService::class.java)
    }

    private val database by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    private val repository by lazy {
        ImageRepository(apiService, database.favoriteDao(), database.cacheDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PicsumComposeTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val composeViewModel: ComposeViewModel = viewModel(
                        factory = ComposeViewModelFactory(repository)
                    )
                    GalleryScreen(viewModel = composeViewModel)
                }
            }
        }
    }
}

// ---- Tema Material 3 personalizado ----

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFFCE93D8),
    onSecondary = Color(0xFF4A148C),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    error = Color(0xFFEF5350)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF7B1FA2),
    onSecondary = Color.White,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    error = Color(0xFFD32F2F)
)

@Composable
fun PicsumComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
