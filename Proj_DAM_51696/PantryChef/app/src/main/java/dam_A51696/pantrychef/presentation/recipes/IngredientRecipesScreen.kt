package dam_A51696.pantrychef.presentation.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.White

/**
 * Ecrã que mostra todas as receitas que usam um determinado ingrediente
 *
 * @param onNavigateBack função chamada ao clicar no botão de voltar
 * @param onNavigateToRecipe função chamada para abrir os detalhes de uma receita da lista
 * @param viewModel viewmodel injetado pelo hilt para gerir os dados do ecrã
 */
@Composable
fun IngredientRecipesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    viewModel: IngredientRecipesViewModel = hiltViewModel()
) {
    // fica sempre a ler o estado do viewmodel para atualizar a interface
    val state by viewModel.state.collectAsState()
    // o esqueleto do ecrã com a cor de fundo bege
    Scaffold(containerColor = CreamBackground) { padding ->
        // organiza tudo numa coluna, respeitando as margens do sistema
        Column(
            modifier = Modifier
            .fillMaxSize()
            .displayCutoutPadding()
            .padding(
                top = 24.dp
            )
        ) {

            // cabeçalho do ecrã (botão de voltar + título)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // botão circular de voltar para trás
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp).background(White, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ForestGreen)
                }

                // dá um espacinho entre o botão e o título
                Spacer(modifier = Modifier.width(16.dp))
                // gera o título de acordo com o estado do ecrã
                val title = if (state is IngredientRecipesState.Success) {
                    // põe a primeira letra do ingrediente em maiúscula
                    "Recipes with ${(state as IngredientRecipesState.Success).ingredientName.replaceFirstChar { it.uppercase() }}"
                } else "Loading..."
                // texto do título
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            }
            // verifica qual é o estado atual para saber o que desenhar
            when (state) {
                // se estiver a carregar, desenha a rodinha verde no centro
                is IngredientRecipesState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ForestGreen)
                }

                // se der erro, escreve a vermelho no centro
                is IngredientRecipesState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = (state as IngredientRecipesState.Error).message, color = Color.Red)
                }

                // se teve sucesso, desenha a lista em grelha
                is IngredientRecipesState.Success -> {
                    // apanha a lista do estado
                    val recipes = (state as IngredientRecipesState.Success).recipes

                    // desenha a grelha com 2 colunas
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // cria um cartão por cada receita
                        items(recipes) { recipe ->
                            // uso o cartão reutilizável das outras páginas
                            RecipeGridCard(
                                recipe = recipe,
                                onClick = { onNavigateToRecipe(recipe.idMeal) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Criei este ficheiro para fazer o ecrã de Receitas por Ingrediente
 * Serve para desenhar a interface que lista as receitas de um ingrediente em grelha,
 * quando o utilizador clica em "Ver mais" na página principal das receitas
 *
 * Funções e componentes criados:
 * - IngredientRecipesScreen:
 *      É a função principal da interface. Recebe o estado do ViewModel e desenha
 *      o botão de voltar, o título dinâmico e uma grelha (LazyVerticalGrid) com os cartões
 *      das receitas
 */