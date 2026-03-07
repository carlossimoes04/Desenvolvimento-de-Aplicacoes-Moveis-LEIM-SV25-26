package dam_a51696.recipebuddy

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dam_a51696.recipebuddy.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity of the application that hosts the navigation components.
 * 
 * This activity manages the [NavController], top app bar, and bottom navigation.
 * It also handles the visibility of the bottom navigation bar based on the current destination.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Top-level destinations (no back button)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.search_fragment, R.id.favorites_fragment)
        )
        
        // Setup toolbar with navigation
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)
        
        // Setup bottom navigation
        binding.bottomNavigationView.setupWithNavController(navController)
        
        // Hide bottom navigation on detail screen
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detail_fragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }
}
