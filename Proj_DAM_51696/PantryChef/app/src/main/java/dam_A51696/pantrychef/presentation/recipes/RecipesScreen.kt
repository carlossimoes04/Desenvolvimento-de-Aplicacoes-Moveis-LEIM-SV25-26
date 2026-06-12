package dam_A51696.pantrychef.presentation.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.GrayText
import dam_A51696.pantrychef.presentation.theme.LightForestGreen
import dam_A51696.pantrychef.presentation.theme.PrimaryOrange
import dam_A51696.pantrychef.presentation.theme.LightOrange
import dam_A51696.pantrychef.presentation.theme.White

@Composable
fun RecipesScreen(
    onNavigateToRecipe: (String) -> Unit,
    onNavigateToIngredientViewMore: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: RecipesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val collapsedCategories by viewModel.collapsedCategories.collectAsState()

    Scaffold(
        containerColor = CreamBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is RecipesUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ForestGreen)
                }
                is RecipesUiState.Error -> {
                    Text(text = "Error: ${state.message}", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                is RecipesUiState.Success -> {
                    RecipesContent(
                        bestMatch = state.bestMatch,
                        bestMatchUsedIngredients = state.bestMatchUsedIngredients,
                        groupedRecipes = state.groupedRecipes,
                        noRecipeIngredients = state.noRecipeIngredients, // passa os ingredientes sem receitas para o conteúdo
                        collapsedCategories = collapsedCategories, // passa o estado para o conteúdo
                        onToggleCategory = { viewModel.toggleCategory(it) }, // envia a ação de clique para o ViewModel
                        onNavigateToSearch = onNavigateToSearch,
                        onNavigateToRecipe = onNavigateToRecipe,
                        onNavigateToIngredientViewMore = onNavigateToIngredientViewMore,
                        paddingValues = padding
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesContent(
    bestMatch: Recipe?,
    bestMatchUsedIngredients: List<String>,
    groupedRecipes: Map<String, List<Recipe>>,
    noRecipeIngredients: List<String>,
    collapsedCategories: Set<String>, // recebe o estado de colapso das categorias
    onToggleCategory: (String) -> Unit, // recebe a ação de clicar
    onNavigateToSearch: () -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    onNavigateToIngredientViewMore: (String) -> Unit,
    paddingValues: PaddingValues
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 24.dp, 
            end = 24.dp,
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding()
        )
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "What to Cook?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = "Based on your pantry items",
                        fontSize = 14.sp,
                        color = GrayText
                    )
                }
                IconButton(
                    onClick = { onNavigateToSearch() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(White, CircleShape)
                ) {
                    Icon(Icons.Outlined.Search, contentDescription = "Search", tint = ForestGreen)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (noRecipeIngredients.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .background(LightOrange, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "We couldn't find recipes for: ${noRecipeIngredients.joinToString(", ")}",
                        color = ForestGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }


        if (bestMatch != null) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "USE THESE FIRST",
                        color = GrayText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
                BestMatchCard(
                    recipe = bestMatch,
                    usedIngredients = bestMatchUsedIngredients,
                    onClick = {
                        onNavigateToRecipe(bestMatch.idMeal)
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        item {
            Text(
                "DISCOVER MORE",
                color = GrayText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (groupedRecipes.isNotEmpty()) {
            // para cada categoria (ingrediente -> lista)
            groupedRecipes.forEach { (ingredientName, recipesList) ->
                val isExpanded = !collapsedCategories.contains(ingredientName) // vê se está aberto

                // cabeçalho da categoria
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleCategory(ingredientName) } // avisa o ViewModel que o utilizador clicou
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = ingredientName.replaceFirstChar { it.uppercase() }, // põe a primeira letra maiúscula
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand/Collapse",
                            tint = GrayText
                        )
                    }
                }

                // se estiver aberto, desenha a grelha das receitas
                if (isExpanded) {
                    val recipesToShow = recipesList.take(6) // mostra um máximo de 6 receitas nesta aba
                    val pairs = recipesToShow.chunked(2) // divide para grelha

                    items(pairs.size) { index ->
                        val pair = pairs[index]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            RecipeGridCard(
                                recipe = pair[0],
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigateToRecipe(pair[0].idMeal) }
                            )
                            if (pair.size > 1) {
                                RecipeGridCard(
                                    recipe = pair[1],
                                    modifier = Modifier.weight(1f),
                                    onClick = { onNavigateToRecipe(pair[1].idMeal) }
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // se existirem mais do que 6 receitas, aparece o botão VIEW MORE
                    if (recipesList.size > 6) {
                        item {
                            Text(
                                text = "View More",
                                color = PrimaryOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToIngredientViewMore(ingredientName) } // vai para o ecrã novo
                                    .padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        } else {
            // mostra a msg de que não existem receitas para os ingredientes a expirar
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nothing to show. Add ingredients to your Pantry.",
                        color = GrayText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// cartão da receita com melhor match para os ingredientes do utilizador
@Composable
fun BestMatchCard(
    recipe: Recipe,
    usedIngredients: List<String>,
    onClick: () -> Unit
) {
    // container principal
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // ocupa toda a largura e permite clicar
        shape = RoundedCornerShape(24.dp), // cantos arredondados
        colors = CardDefaults.cardColors(containerColor = White), // cor de fundo branca
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // sombra
    ) {
        // coluna que agrupa a imagem e textos
        Column {
            // uma box que permite sobrepôr a etiqueta "BEST MATCH" em cima da imagem
            Box {
                AsyncImage( // faz o pedido à API para carregar a imagem
                    model = recipe.strMealThumb, // URL da API
                    contentDescription = recipe.strMeal, // para acessibilidade
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), // altura fixa de 200dp
                    contentScale = ContentScale.Crop // corta as bordas para preencher o espaço total sem distorcer
                )
                Surface( // etiqueta "BEST MATCH" que sobrepõe a imagem
                    color = LightForestGreen, // cor de fundo
                    shape = RoundedCornerShape(12.dp), // cantos arredondados
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomStart) // posiciona no canto inferior esquerdo
                ) {
                    Text( // texto da etiqueta
                        text = "BEST MATCH",
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(20.dp)) { // área abaixo da imagem que contém as letras
                Text( // título da receita
                    text = recipe.strMeal,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // espaço extra para manter a proporção da UI agradável
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.Divider(color = CreamBackground) // linha divisória
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // lógica para formatar o texto que vai ser lido (Ex: "USES: CHICKEN, GARLIC")
                    val textToShow = if (usedIngredients.isEmpty()) {
                        "USES 0 PANTRY ITEMS"
                    } else {
                        "USES: " + usedIngredients.joinToString(", ").uppercase()
                    }

                    Text(
                        text = textToShow,
                        color = GrayText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1, // garante que o texto não ocupa mais do que uma linha para não partir o cartão
                        overflow = TextOverflow.Ellipsis, // coloca "..." no final caso a lista de ingredientes não caiba no ecrã
                        modifier = Modifier.weight(1f).padding(end = 8.dp) // empurra as bolinhas da decoração para a direita
                    )

                    // bolas decorativas que simulam os ingredientes
                    Row(horizontalArrangement = Arrangement.spacedBy(-8.dp)) {
                        Box(modifier = Modifier.size(24.dp).background(PrimaryOrange, CircleShape))
                        Box(modifier = Modifier.size(24.dp).background(LightOrange, CircleShape))
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeGridCard(recipe: Recipe, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = recipe.strMealThumb,
                contentDescription = recipe.strMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = recipe.strMeal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2
                )
            }
        }
    }
}
