package dam_A51696.pantrychef.presentation.favorites

// imports do Jetpack Compose para Layouts (Box, Row, Column, LazyColumn)
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
import androidx.compose.foundation.lazy.LazyColumn
// importss de componentes visuais do Material Design 3
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Hilt ViewModel para instanciar automaticamente o ViewModel associado
import androidx.hilt.navigation.compose.hiltViewModel
// modelo da receita
import dam_A51696.pantrychef.domain.model.Recipe
// reutiliza-se o componente RecipeGridCard feito para a página Recipes
import dam_A51696.pantrychef.presentation.recipes.RecipeGridCard
// importa-se as cores personalizadas do ficheiro Theme
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.GrayText

@Composable
fun FavoritesScreen(
    // parâmetro: uma função que diz ao sistema o que fazer quando se clica numa receita (navegar)
    onNavigateToRecipe: (String) -> Unit,
    // instancia o FavoritesViewModel de forma automática com o Hilt
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    // fica a "escutar" constantemente (collectAsState) qualquer mudança de estado emitida pelo ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // o Scaffold define a área base do ecrã (e permite adicionar barras de navegação futuramente)
    Scaffold(
        containerColor = CreamBackground
    ) { padding ->
        // uma caixa (Box) que ocupa o ecrã todo e não se sobrepõe ao menu de baixo (devido ao padding)
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // qual é o estado atual que vem do ViewModel?
            when (val state = uiState) {

                // se estiver a carregar
                is FavoritesUiState.Loading -> {
                    // mostra-se a "rodinha" de loading no centro do ecrã
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ForestGreen)
                }

                // se der erro
                is FavoritesUiState.Error -> {
                    // mostra-se o texto de erro a vermelho no centro do ecrã
                    Text(text = "Error: ${state.message}", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }

                // se tiver carregado os dados com sucesso
                is FavoritesUiState.Success -> {
                    // chama-se a função que desenha a lista visual, passando-lhe a lista de receitas
                    FavoritesContent(
                        recipes = state.recipes,
                        onNavigateToRecipe = onNavigateToRecipe
                    )
                }
            }
        }
    }
}

// sub-componente (privado) responsável por desenhar apenas a lista visual de favoritos
@Composable
fun FavoritesContent(recipes: List<Recipe>, onNavigateToRecipe: (String) -> Unit) {
    // LazyColumn é equivalente à RecyclerView
    // apenas carrega para a memória as receitas visíveis no ecrã
    LazyColumn(
        // ocupa todo o espaço disponível
        modifier = Modifier.fillMaxSize(),
        // define as margens interiores: 24dp dos lados e 80dp em baixo (para não ficar tapado pelo bottom menu)
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 80.dp)
    ) {

        // o cabeçalho (título) da página
        item {
            // espaçamento em branco de 32dp
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Favorite Recipes",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Text(
                text = "Your saved meals",
                fontSize = 14.sp,
                color = GrayText
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // se a lista não tiver nenhuma receita favorita
        if (recipes.isEmpty()) {
            item {
                // coluna para alinhar os textos no centro
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "No Favorites Yet", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Save your favorite recipes so they appear here.",
                        fontSize = 14.sp, color = GrayText, textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // se existirem receitas favoritas para mostrar
        if (recipes.isNotEmpty()) {
            // usa-se a lista de receitas e divide-se em pequenos grupos de 2 (ex: [A,B], [C,D])
            val pairs = recipes.chunked(2)

            // ciclo para iterar sobre cada par (cada linha de 2 receitas)
            items(pairs.size) { index ->
                val pair = pairs[index] // obtém o par atual

                // uma linha (Row) horizontal que distribui o espaço por igual
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    // colaca-se a 1ª receita do par à esquerda
                    // reutiliza-se o "RecipeGridCard"
                    // o Modifier.weight(1f) garante que ocupa metade do ecrã
                    RecipeGridCard(recipe = pair[0], modifier = Modifier.weight(1f), onClick = { onNavigateToRecipe(pair[0].idMeal) })

                    // verifica-se se há uma 2ª receita neste par (se a lista total era ímpar, o último par tem apenas 1 elemento)
                    if (pair.size > 1) {
                        // se houver, coloca-se à direita
                        RecipeGridCard(recipe = pair[1], modifier = Modifier.weight(1f), onClick = { onNavigateToRecipe(pair[1].idMeal) })
                    } else {
                        // se não houver 2ª receita, coloca-se um espaço transparente vazio para manter o alinhamento
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                // espaçamento vertical antes da próxima linha da grelha
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}