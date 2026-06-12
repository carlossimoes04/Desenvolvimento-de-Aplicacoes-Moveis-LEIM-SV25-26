package dam_A51696.pantrychef.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dam_A51696.pantrychef.presentation.recipes.RecipeGridCard
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.GrayText
import dam_A51696.pantrychef.presentation.theme.White

/**
 * Ecrã de pesquisa manual de receitas
 *
 * Permite ao utilizador escrever o nome de um ingrediente (ex: Chicken) e
 * apresenta as receitas relacionadas numa grelha, dependendo do estado
 *
 * @param onNavigateBack função chamada no botão esquerdo para voltar para a página anterior
 * @param onNavigateToRecipe função chamada ao clicar numa receita para abrir os seus detalhes
 * @param viewModel viewmodel injetado pelo hilt para gerir o texto pesquisado e os resultados da API
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    // fica a escutar a situação atual da pesquisa (Idle, Loading, Error, Success)
    val state by viewModel.uiState.collectAsState()
    // guarda o texto que está escrito na barra de pesquisa neste momento
    val searchQuery by viewModel.searchQuery.collectAsState()

    val focusManager = LocalFocusManager.current // para esconder o teclado

    // o fundo bege claro
    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
        containerColor = CreamBackground
    ) { padding ->
        // organiza tudo verticalmente, respeitando as margens do sistema
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // secção superior: botão de voltar e barra de pesquisa lado a lado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // botão circular branco com a seta verde para voltar
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(48.dp).background(White, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ForestGreen)
                }
                Spacer(modifier = Modifier.width(16.dp))

                // caixa de pesquisa com botão de pesquisa à direita
                TextField(
                    value = searchQuery,
                    // quando a pessoa escreve uma letra, atualiza a palavra no ViewModel
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    // texto fantasma que aparece quando a caixa está vazia
                    placeholder = { Text("Search ingredient (e.g. Chicken)", color = GrayText, fontSize = 14.sp) },
                    singleLine = true, // não deixa fazer enter e mudar de linha
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp), // faz uma caixa com cantos arredondados
                    trailingIcon = {
                        // lupa no lado direito da caixa de texto
                        IconButton(onClick = { viewModel.searchRecipes() }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = ForestGreen)
                        }
                    }
                )
            }

            // depois do cabeçalho, desenha o meio do ecrã dependendo da situação
            when (state) {
                is SearchUiState.Idle -> {
                    // situação parada: mostra só uma instrução cinzenta a meio
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Type an ingredient and press search", color = GrayText)
                    }
                }
                is SearchUiState.Loading -> {
                    // situação a carregar: a rodinha verde enquanto aguardamos que
                    // cheguem os resultados
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ForestGreen)
                    }
                }
                is SearchUiState.Error -> {
                    // se falhar a chamada à API atira aqui o erro a vermelho
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = (state as SearchUiState.Error).message, color = Color.Red)
                    }
                }
                is SearchUiState.Success -> {
                    // se correr tudo bem, desempacota a lista de receitas
                    val recipes = (state as SearchUiState.Success).recipes
                    if (recipes.isEmpty()) {
                        // se a pesquisa deu sucesso, mas devolveram 0 receitas
                        // (ex: procurou "asdasdasd")
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No recipes found for '$searchQuery'", color = GrayText)
                        }
                    } else {
                        // desenha a grelha bonita de 2 colunas
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(recipes) { recipe ->
                                // usa o cartão reutilizável das receitas para desenhar e tratar do clique
                                RecipeGridCard(recipe = recipe, onClick = { onNavigateToRecipe(recipe.idMeal) })
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Criei este ficheiro para fazer o ecrã de pesquisa manual
 *
 * Serve para que a pessoa possa procurar receitas livremente, escrevendo
 * o nome de um ingrediente e carregando na lupa, sem estar dependente apenas da despensa
 *
 * Funções e componentes criados:
 * - SearchScreen:
 *      A única e principal função do ficheiro. Fica a escutar o estado (SearchUiState) e a
 *      palavra-chave (searchQuery) do ViewModel.
 *      Tem uma barra no topo com o botão de voltar e o campo de pesquisa (TextField).
 *      Em baixo, avalia os quatro estados possíveis: Idle (mensagem inicial com dicas),
 *      Loading (roda a carregar), Error (mensagem a vermelho) e Success.
 *      No estado Success, verifica se encontrou alguma coisa. Se sim, usa um LazyVerticalGrid para
 *      desenhar a grelha de cartões usando o componente partilhado RecipeGridCard
 */