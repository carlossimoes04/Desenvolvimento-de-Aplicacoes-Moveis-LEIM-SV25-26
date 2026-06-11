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

/**
 * Componente Composable que serve de ecrã para exibir a lista de receitas favoritas do utilizador
 *
 * Observa o estado proveniente da [FavoritesViewModel] e desenha os componentes adequados:
 * - um indicador de progresso,
 * - um ecrã de erro,
 * - um ecrã vazio ou a grelha de receitas favoritas
 *
 * @param onNavigateToRecipe Callback invocado quando o utilizador clica numa receita
 * para ver os seus detalhes
 * @param viewModel A instância de [FavoritesViewModel] que fornece o estado de UI
 */
@Composable
fun FavoritesScreen(
    // parâmetro: uma função que diz ao sistema o que fazer quando se clica numa receita (navegar)
    onNavigateToRecipe: (String) -> Unit,
    // instancia o FavoritesViewModel de forma automática com o Hilt
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    // fica a "escutar" constantemente (collectAsState) qualquer mudança de estado emitida pelo ViewModel
    val uiState by viewModel.uiState.collectAsState()

    /*
    A função collectAsState() é essencial no Jetpack Compose para converter um fluxo de dados
    assíncrono (como Flow ou StateFlow) num State do Compose

    Isso permite que a interface reaja e seja atualizada (recomposta) automaticamente
    sempre que o dado mudar
     */

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
                    Text(text = "Error: ${state.message}", color = Color.Red,
                        modifier = Modifier.align(Alignment.Center))
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

/**
 * Componente Composable responsável por desenhar a lista de receitas
 * favoritas numa grelha de duas colunas
 *
 * @param recipes Lista de receitas favoritas obtidas da base de dados
 * @param onNavigateToRecipe Callback invocado para navegar até à página da receita selecionada
 */
@Composable
fun FavoritesContent(recipes: List<Recipe>, onNavigateToRecipe: (String) -> Unit) {
    // LazyColumn é equivalente à RecyclerView
    // apenas carrega para a memória as receitas visíveis no ecrã
    LazyColumn(
        // ocupa todo o espaço disponível
        modifier = Modifier.fillMaxSize(),
        // define as margens interiores: 24dp dos lados e 80dp em baixo
        // (para não ficar tapado pelo bottom menu)
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
                    Text(
                        text = "No Favorites Yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Save your favorite recipes so they appear here.",
                        fontSize = 14.sp,
                        color = GrayText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    // colaca-se a 1ª receita do par à esquerda
                    // reutiliza-se o "RecipeGridCard"
                    // o Modifier.weight(1f) garante que ocupa metade do ecrã
                    RecipeGridCard(recipe = pair[0], modifier = Modifier.weight(1f),
                        onClick = { onNavigateToRecipe(pair[0].idMeal) })

                    // verifica-se se há uma 2ª receita neste par (se a lista total era ímpar,
                    // o último par tem apenas 1 elemento)
                    if (pair.size > 1) {
                        // se houver, coloca-se à direita
                        RecipeGridCard(recipe = pair[1], modifier = Modifier.weight(1f),
                            onClick = { onNavigateToRecipe(pair[1].idMeal) })
                    } else {
                        // se não houver 2ª receita, coloca-se um espaço transparente vazio
                        // para manter o alinhamento
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                // espaçamento vertical antes da próxima linha da grelha
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


/**
 * Desenvolvi este ecrã com o intuito de apresentar uma lista organizada de todas
 * as receitas que o utilizador guardou como favoritas na aplicação Pantry Chef
 *
 * Decisões de Implementação
 * - Scaffold e Padding:
 *      Optei por usar um Scaffold para definir o contentor base do ecrã, respeitando
 *      o padding do sistema para garantir que os elementos não colidem com o menu inferior
 * - Tratamento de Estados (when):
 *      Estruturei o ecrã para lidar com os três estados possíveis de forma reativa (Loading,
 *      Error, Success), proporcionando feedbacks adequados a cada um
 * - chunked(2) e Grelha Bidimensional:
 *      Decidi particionar a lista de receitas em blocos de dois elementos para simular uma
 *      grelha bidimensional numa LazyColumn, contornando a necessidade de uma LazyVerticalGrid
 * - Reutilização de Componentes:
 *      Reutilizei o componente RecipeGridCard para desenhar cada receita da lista de forma
 *      homogénea, tirando partido do peso (Modifier.weight(1f)) para preencher a linha uniformemente
 */