package dam_A51696.pantrychef.presentation.recipes

import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
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

/**
 * Ecrã de detalhes da receita
 *
 * Fica a ouvir o estado do viewmodel e gere o que deve aparecer no ecrã
 * (se está a carregar, se deu erro, ou se já tem os dados)
 *
 * @param recipeId o id da receita que recebemos por navegação
 * @param onNavigateBack função para voltar para trás e sair do ecrã
 * @param viewModel viewmodel injetado pelo hilt para irmos buscar os dados
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String?,
    onNavigateBack: () -> Unit, // parâmetro para retroceder o ecrã
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    // fica a ouvir o estado atual da receita no viewmodel
    val state by viewModel.state.collectAsState()
    // recebe o estado para saber se é um favorito ou não (coração cheio ou vazio)
    val isFavorite by viewModel.isFavorite.collectAsState()
    // recebe a lista de ingredientes da despensa
    val pantryIngredients by viewModel.pantryIngredients.collectAsState()

    // verifica em que estado está a app
    when (state) {
        // se estiver a carregar, mete a rodinha no meio do ecrã
        is RecipeDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ForestGreen)
            }
        }
        // se houver erro, mete texto vermelho
        is RecipeDetailState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (state as RecipeDetailState.Error).message, color = Color.Red)
            }
        }
        // se carregou a receita toda, vai chamar a interface principal
        is RecipeDetailState.Success -> {
            // chama a função que desenha a interface passando os dados
            val recipe = (state as RecipeDetailState.Success).recipe
            RecipeDetailContent(
                recipe = recipe,
                isFavorite = isFavorite,
                pantryIngredients = pantryIngredients,
                onAddMissing = { viewModel.addMissingIngredients() },
                onNavigateBack = onNavigateBack,
                onToggleFavorite = { viewModel.toggleFavorite() }
            )
        }
    }
}

/**
 * Interface visual completa dos detalhes da receita (quando carrega com sucesso)
 *
 * @param recipe os detalhes reais e inteiros da receita (nome, foto, passos)
 * @param isFavorite variável boleana que diz se o coração está marcado
 * @param onAddMissing função chamada no botão de adicionar os ingredientes à lista de compras
 * @param onNavigateBack função chamada no botão esquerdo para voltar à página anterior
 * @param onToggleFavorite função chamada no coração para adicionar ou tirar dos favoritos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailContent(
    recipe: RecipeDetail,
    isFavorite: Boolean,
    pantryIngredients: List<String>,
    onAddMissing: () -> Unit,
    onNavigateBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    // gere o estado da aba de baixo que desliza sobre a imagem
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            // começa a aba meio-encolhida
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

        // a parte fixa de trás (imagem grande e botões no topo)
        content = { _ ->
            Box(modifier = Modifier.fillMaxSize()) {
                // biblioteca coil que carrega a imagem da api pelo URL
                AsyncImage(
                    model = recipe.strMealThumb,
                    contentDescription = "Recipe Image",
                    modifier = Modifier.fillMaxWidth().height(500.dp), // altura grande
                    contentScale = ContentScale.Crop // corta as pontas para preencher o ecrã
                )

                // fila lá no cimo para os botões por cima da foto
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // botão para voltar (bolinha com transparência preta)
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }

                    // botão do coração (também em bolinha com transparência)
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            // ícone dinâmico: coração cheio ou só o contorno dependendo do estado
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            // fica laranja se for favorito, senão fica branco normal
                            tint = if (isFavorite) PrimaryOrange else White
                        )
                    }
                }
            }
        },

        // a parte que desliza para cima (título, instruções, botão da lista de compras e lista de ingredientes)
        sheetContent = {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                // título enorme da receita
                Text(text = recipe.strMeal, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ForestGreen, lineHeight = 34.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // texto corrido das instruções que vêm da api
                Text(text = recipe.strInstructions, color = GrayText, fontSize = 14.sp, lineHeight = 22.sp)
                Spacer(modifier = Modifier.height(24.dp))

                // botão verde colocar ingredientes em falta para a shopping list
                Button(
                    onClick = onAddMissing,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    // carrinho de compras
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add missing items to list", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ciclo que percorre todos os ingredientes desta receita específica e os desenha
                recipe.ingredients.forEach { (name, measure) ->
                    val isIngredientInPantry = pantryIngredients.contains(name.lowercase())
                    // chama o componente que faz a row de um ingrediente
                    IngredientItemRow(name = name, amount = measure, inPantry = isIngredientInPantry)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    )
}

/**
 * Componente que desenha cada linha da lista dos ingredientes precisos
 *
 * @param name o nome do ingrediente
 * @param amount a quantidade que a receita pede (medida)
 * @param inPantry variável que vai servir depois para marcar se já temos na despensa
 */
@Composable
fun IngredientItemRow(name: String, amount: String, inPantry: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // bolinha esquerda para meter uma cor ou ícone
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(if (inPantry) LightForestGreen else CreamBackground),
            contentAlignment = Alignment.Center
        ) {
            // se tiver na despensa devia mostrar ícone verde
            if (inPantry) Icon(Icons.Default.List, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        // meio da linha com o nome
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
            if (inPantry) {
                // texto verde a dizer que já temos
                Text(text = "IN PANTRY", color = ForestGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        // canto direito com o peso/litros do ingrediente
        Text(text = amount, color = GrayText, fontSize = 14.sp)
    }
}

/**
 * Criei este ficheiro para fazer o ecrã dos detalhes de uma receita
 *
 * Serve para mostrar a imagem grande da receita, as instruções e a lista
 * de ingredientes precisos, permitindo também adicionar aos favoritos ou à lista de compras
 *
 * Funções e componentes criados:
 * - RecipeDetailScreen:
 *      É a função de entrada. Ouve o estado do ViewModel e decide se mostra a
 *      roda a carregar, a mensagem de erro ou o conteúdo da receita com sucesso
 * - RecipeDetailContent:
 *      É o componente principal que desenha a interface (BottomSheetScaffold).
 *      Tem uma parte fixa por trás (imagem grande, botão voltar e favoritos) e uma aba
 *      que desliza por cima com o nome, instruções e botão de compras
 * - IngredientItemRow:
 *      É o design de cada linha da lista de ingredientes, com uma bola do lado
 *      esquerdo, o nome do ingrediente e a quantidade necessária no lado direito
 */