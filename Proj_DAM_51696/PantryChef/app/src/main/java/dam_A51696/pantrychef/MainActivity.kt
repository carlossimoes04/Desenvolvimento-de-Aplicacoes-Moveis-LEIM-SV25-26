package dam_A51696.pantrychef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dam_A51696.pantrychef.presentation.navigation.AppNavigation
import dam_A51696.pantrychef.presentation.theme.PantryChefTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PantryChefTheme {
                AppNavigation()
            }
        }
    }
}