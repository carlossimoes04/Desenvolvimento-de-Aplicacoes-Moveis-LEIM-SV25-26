package dam_A51696.pantrychef.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dam_A51696.pantrychef.presentation.favorites.FavoritesScreen
import dam_A51696.pantrychef.presentation.pantry.PantryScreen
import dam_A51696.pantrychef.presentation.recipes.RecipeDetailScreen
import dam_A51696.pantrychef.presentation.recipes.RecipesScreen
import dam_A51696.pantrychef.presentation.shopping.ShoppingListScreen
import dam_A51696.pantrychef.presentation.auth.LoginScreen
import dam_A51696.pantrychef.presentation.auth.SignUpScreen
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Login : Screen("login", "Login", null)
    object SignUp : Screen("signup", "Sign Up", null)
    object Pantry : Screen("pantry", "Pantry", Icons.Default.List)
    object Recipes : Screen("recipes", "Recipes", Icons.Default.Star)
    object Shopping : Screen("shopping", "Shopping", Icons.Default.ShoppingCart)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    object RecipeDetail : Screen("recipe_detail/{recipeId}", "Recipe Detail", null) {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }
}

val bottomNavItems = listOf(
    Screen.Pantry,
    Screen.Recipes,
    Screen.Shopping,
    Screen.Favorites
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDest = if (currentUser != null) Screen.Pantry.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Show bottom bar only on main screens
            if (currentRoute in bottomNavItems.map { it.route }) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreen,
                                selectedTextColor = ForestGreen,
                                indicatorColor = ForestGreen.copy(alpha = 0.2f)
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.Pantry.route) {
                            popUpTo(0) // Clear backstack
                        }
                    }
                )
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onSignUpSuccess = {
                        navController.navigate(Screen.Pantry.route) {
                            popUpTo(0) // Clear backstack
                        }
                    }
                )
            }
            composable(Screen.Pantry.route) {
                PantryScreen()
            }
            composable(Screen.Recipes.route) {
                RecipesScreen(
                    onNavigateToRecipe = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    }
                )
            }
            composable(Screen.Shopping.route) {
                ShoppingListScreen()
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    // esta função diz à AppNavigation para mudar de ecrã quando o Utilizador clica numa receita
                    onNavigateToRecipe = { recipeId ->
                        // cria a rota para o RecipeDetail usando o ID da receita escolhida
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    }
                )
            }
            composable(Screen.RecipeDetail.route) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")
                RecipeDetailScreen(
                    recipeId = recipeId,
                    // o navController encarrega-se de retroceder um passo na navegação (Pop backstack)
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
