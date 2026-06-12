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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dam_A51696.pantrychef.presentation.auth.AuthViewModel
import dam_A51696.pantrychef.presentation.favorites.FavoritesScreen
import dam_A51696.pantrychef.presentation.pantry.PantryScreen
import dam_A51696.pantrychef.presentation.recipes.RecipeDetailScreen
import dam_A51696.pantrychef.presentation.recipes.RecipesScreen
import dam_A51696.pantrychef.presentation.shopping.ShoppingListScreen
import dam_A51696.pantrychef.presentation.auth.LoginScreen
import dam_A51696.pantrychef.presentation.auth.SignUpScreen
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.recipes.IngredientRecipesScreen
import dam_A51696.pantrychef.presentation.search.SearchScreen

/**
 * Representa um destino de navegação (ecrã) na aplicação Pantry Chef
 *
 * Cada ecrã possui uma rota única, um título associado e, opcionalmente,
 * um ícone para exibição na barra de navegação inferior
 *
 * @property route Rota de navegação única
 * @property title Nome amigável do ecrã para exibição
 * @property icon Ícone correspondente do Material Design, se aplicável
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Login : Screen("login", "Login", null) // página de login
    object SignUp : Screen("signup", "Sign Up", null) // página de signup
    object Pantry : Screen("pantry", "Pantry", Icons.Default.List) // página da despensa
    object Recipes : Screen("recipes", "Recipes", Icons.Default.Star) // página de receitas
    object Shopping : Screen("shopping", "Shopping", Icons.Default.ShoppingCart) // página da lista de compras
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite) // página dos favoritos
    object RecipeDetail : Screen("recipe_detail/{recipeId}", "Recipe Detail", null) {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    } // página de detalhes da receita
    object IngredientRecipes : Screen("ingredient_recipes/{ingredientName}", "Ingredient Recipes", null) {
        fun createRoute(ingredientName: String) = "ingredient_recipes/$ingredientName"
    } // página de receitas que utilizam um ingrediente específico
    object Search : Screen("search", "Search", null) // página de pesquisa
}

/**
 * Lista dos ecrãs principais que farão parte da barra de navegação inferior da aplicação
 */
val bottomNavItems = listOf(
    Screen.Pantry,
    Screen.Recipes,
    Screen.Shopping,
    Screen.Favorites
)


/**
 * Componente Composable central encarregue de configurar o NavHost da aplicação,
 * gerir a barra de navegação inferior (bottomBar) e declarar as rotas válidas de navegação
 *
 * @param authViewModel A ViewModel de autenticação injetada para verificar o estado da sessão
 */
@Composable
fun AppNavigation(authViewModel: AuthViewModel = hiltViewModel()) {
    // cria e recorda o controlador de navegação para gerir o histórico de ecrãs
    val navController = rememberNavController()
    // obtém a instância atual do firebase auth para verificar se o utilizador está autenticado
    val isUserLoggedIn = authViewModel.isUserLoggedIn
    // define o ecrã inicial: se estiver autenticado vai para a despensa, senão vai para o login
    val startDest = if (isUserLoggedIn) Screen.Pantry.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            // obtém a entrada atual da pilha de navegação para saber qual o ecrã ativo
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            // extrai a rota do ecrã que está a ser visualizado no momento
            val currentRoute = navBackStackEntry?.destination?.route

            // mostra a barra inferior apenas nos ecrãs principais
            if (currentRoute in bottomNavItems.map { it.route }) {
                NavigationBar (
                    containerColor = dam_A51696.pantrychef.presentation.theme.CreamBackground
                ) {
                    // percorre cada um dos ecrãs definidos para o menu inferior
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            // desenha o ícone do ecrã
                            icon = { Icon(screen.icon!!,
                                contentDescription = screen.title) },
                            // desenha o texto descritivo por baixo do ícone
                            label = { Text(screen.title) },
                            // verifica se o ecrã do botão coincide com o ecrã atualmente ativo
                            selected = currentRoute == screen.route,
                            // define as cores personalizadas para o estado selecionado (verde floresta)
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreen,
                                selectedTextColor = ForestGreen,
                                indicatorColor = ForestGreen.copy(alpha = 0.2f)
                            ),
                            onClick = {
                                // executa a navegação para o ecrã clicado
                                navController.navigate(screen.route) {
                                    // redireciona a navegação limpando o histórico até ao ecrã
                                    // inicial para evitar acumulação
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        // guarda o estado do ecrã anterior para poder ser reposto
                                        saveState = true
                                    }
                                    // evita criar múltiplas cópias do mesmo ecrã se o utilizador
                                    // clicar várias vezes
                                    launchSingleTop = true
                                    // repõe o estado previamente guardado ao navegar
                                    // novamente para este ecrã
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // define o contentor de navegação com a rota inicial e o controlador associado
        NavHost(
            navController = navController,
            startDestination = startDest,
            // define a área de desenho aplicando a margem da barra inferior
            modifier = Modifier.padding(innerPadding)
        ) {
            // rota de início de sessão (login)
            composable(Screen.Login.route) {
                LoginScreen(
                    // define a ação ao clicar para ir para o ecrã de registo
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.Pantry.route) {
                            // limpa a pilha de navegação para que o utilizador não consiga voltar
                            // ao login com o botão físico de voltar
                            popUpTo(0)
                        }
                    }
                )
            }
            // rota de registo de conta (sign up)
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    // define a ação ao clicar para voltar ao ecrã de login
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onSignUpSuccess = {
                        navController.navigate(Screen.Pantry.route) {
                            // limpa a pilha de navegação para evitar o retorno acidental
                            // ao formulário de registo
                            popUpTo(0)
                        }
                    }
                )
            }
            // rota do ecrã principal da despensa (pantry)
            composable(Screen.Pantry.route) {
                PantryScreen(
                    onLogoutClick = {
                        // sair da conta
                        authViewModel.logout()
                        // redirecionar para o login
                        navController.navigate(Screen.Login.route) {
                            // limpa a pilha de navegação para evitar o retorno acidental
                            popUpTo(0)
                        }
                    }
                )
            }
            // rota do ecrã principal de receitas (recipes)
            composable(Screen.Recipes.route) {
                RecipesScreen(
                    // navega para o detalhe da receita passando o id correspondente
                    onNavigateToRecipe = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    },
                    // envia o utilizador para o novo ecrã filtrado por ingrediente
                    onNavigateToIngredientViewMore = { ingredientName ->
                        navController.navigate(Screen.IngredientRecipes.createRoute(ingredientName))
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }
            // rota do ecrã da lista de compras (shopping)
            composable(Screen.Shopping.route) {
                ShoppingListScreen()
            }
            // rota do ecrã dos favoritos (favorites)
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    // esta função instrui a navegação a mudar de ecrã quando o utilizador clica
                    // numa receita
                    onNavigateToRecipe = { recipeId ->
                        // cria a rota para o RecipeDetail usando o ID da receita escolhida
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    }
                )
            }
            // rota do ecrã de detalhe da receita
            composable(Screen.RecipeDetail.route) { backStackEntry ->
                // extrai o id da receita passado através da rota de navegação
                val recipeId = backStackEntry.arguments?.getString("recipeId")
                RecipeDetailScreen(
                    recipeId = recipeId,
                    // o navController encarrega-se de retroceder um passo na navegação (Pop backstack)
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            // rota do ecrã de pesquisa global
            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRecipe = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    }
                )
            }
            // rota do ecrã de receitas associadas a um ingrediente
            composable(Screen.IngredientRecipes.route) { backStackEntry ->
                IngredientRecipesScreen(
                    // fecha o ecrã atual e volta ao anterior na pilha
                    onNavigateBack = { navController.popBackStack() },
                    // abre os detalhes da receita a partir da lista de ingredientes
                    onNavigateToRecipe = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    }
                )
            }
        }
    }
}

/**
 * Desenvolvi este componente de navegação central para gerir o fluxo de ecrãs da aplicação
 * e garantir uma navegação fluida entre os ecrãs principais e os fluxos de autenticação
 *
 * Decisões de Implementação
 * - Sealed Class Screen:
 *      Este bloco define de forma centralizada e segura o nome, o ícone e a rota (URL) de cada
 *      ecrã da aplicação, facilitando a navegação e a passagem de parâmetros entre eles
 * - Scaffold com bottomBar:
 *      Implementei uma lógica condicional na bottomBar para mostrar o menu de navegação
 *      inferior apenas nos ecrãs de destino principal (Pantry, Recipes, Shopping, Favorites),
 *      ocultando-o em fluxos como o login ou os detalhes da receita
 * - isUserLoggedIn via AuthViewModel:
 *      Substituí a chamada direta ao FirebaseAuth pela propriedade isUserLoggedIn
 *      da AuthViewModel, eliminando a dependência direta da Firebase na UI e respeitando
 *      o padrão MVVM
 * - PopUpTo e SingleTop:
 *      Configurei as opções de navegação da barra inferior (popUpTo e launchSingleTop)
 *      para evitar a acumulação excessiva de instâncias duplicadas de ecrãs na pilha,
 *      otimizando o consumo de memória do dispositivo
 */