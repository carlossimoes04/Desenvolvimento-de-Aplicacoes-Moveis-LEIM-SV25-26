package dam_A51696.pantrychef.presentation.recipes

import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material.icons.filled.FavoriteBorder // para o ícone não guardado
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.GrayText
import dam_A51696.pantrychef.presentation.theme.LightForestGreen
import dam_A51696.pantrychef.presentation.theme.PrimaryOrange
import dam_A51696.pantrychef.presentation.theme.White
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import dam_A51696.pantrychef.domain.model.RecipeDetail

@OptIn(ExperimentalMaterial3Api::class) // necessário para a API do BottomSheet
@Composable
fun RecipeDetailScreen(
    recipeId: String?,
    onNavigateBack: () -> Unit, // parâmetro para retroceder o ecrã
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState() // recebe-se o estado do favorito

    when (state) {
        is RecipeDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ForestGreen)
            }
        }
        is RecipeDetailState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (state as RecipeDetailState.Error).message, color = Color.Red)
            }
        }
        is RecipeDetailState.Success -> {
            val recipe = (state as RecipeDetailState.Success).recipe
            RecipeDetailContent(
                recipe = recipe,
                isFavorite = isFavorite, // passa-se à UI
                onAddMissing = { viewModel.addMissingIngredients() },
                onNavigateBack = onNavigateBack,
                onToggleFavorite = { viewModel.toggleFavorite() }
            )
        }
    }
}

// função responsável por desenhar no ecrã de detalhes da receita
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailContent(
    recipe: RecipeDetail,
    isFavorite: Boolean,
    onAddMissing: () -> Unit,
    onNavigateBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true // impede que o utilizador o arraste para baixo e o esconda
        )
    )
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        // define a altura padrão visível da gaveta branca (a aba com a barra cinzenta)
        sheetPeekHeight = 400.dp,
        containerColor = Color.Black,
        sheetContainerColor = CreamBackground,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),

        // imagem grande e botões superiores
        content = { _ ->
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = recipe.strMealThumb,
                    contentDescription = "Recipe Image",
                    modifier = Modifier.fillMaxWidth().height(500.dp), // Preenche tudo
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // botão para voltar
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }

                    // botão favoritos
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            // Ícone dinâmico baseado no estado da Base de Dados
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) PrimaryOrange else White
                        )
                    }
                }
            }
        },

        // conteúdo de texto e ingredientes que desliza
        sheetContent = {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                // a tal barra cinzenta visual (drag handle)

                Text(text = recipe.strMeal, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ForestGreen, lineHeight = 34.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = recipe.strInstructions, color = GrayText, fontSize = 14.sp, lineHeight = 22.sp)
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onAddMissing,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add missing items to list", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                recipe.ingredients.forEach { (name, measure) ->
                    IngredientItemRow(name = name, amount = measure, inPantry = false)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    )
}

@Composable
fun IngredientItemRow(name: String, amount: String, inPantry: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(if (inPantry) LightForestGreen else CreamBackground),
            contentAlignment = Alignment.Center
        ) {
             if (inPantry) Icon(Icons.Default.List, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
            if (inPantry) {
                Text(text = "IN PANTRY", color = ForestGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(text = amount, color = GrayText, fontSize = 14.sp)
    }
}
