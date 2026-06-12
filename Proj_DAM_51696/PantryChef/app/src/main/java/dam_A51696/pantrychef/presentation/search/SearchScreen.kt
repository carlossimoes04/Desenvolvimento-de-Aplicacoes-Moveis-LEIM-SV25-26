package dam_A51696.pantrychef.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dam_A51696.pantrychef.presentation.recipes.RecipeGridCard
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.GrayText
import dam_A51696.pantrychef.presentation.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(containerColor = CreamBackground) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Barra de Pesquisa e Botão Voltar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(48.dp).background(White, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ForestGreen)
                }
                Spacer(modifier = Modifier.width(16.dp))

                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    placeholder = { Text("Search ingredient (e.g. Chicken)", color = GrayText, fontSize = 14.sp) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.searchRecipes() }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = ForestGreen)
                        }
                    }
                )
            }

            // Resultados da Pesquisa
            when (state) {
                is SearchUiState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Type an ingredient and press search", color = GrayText)
                    }
                }
                is SearchUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ForestGreen)
                    }
                }
                is SearchUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = (state as SearchUiState.Error).message, color = Color.Red)
                    }
                }
                is SearchUiState.Success -> {
                    val recipes = (state as SearchUiState.Success).recipes
                    if (recipes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No recipes found for '$searchQuery'", color = GrayText)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(recipes) { recipe ->
                                RecipeGridCard(recipe = recipe, onClick = { onNavigateToRecipe(recipe.idMeal) })
                            }
                        }
                    }
                }
            }
        }
    }
}