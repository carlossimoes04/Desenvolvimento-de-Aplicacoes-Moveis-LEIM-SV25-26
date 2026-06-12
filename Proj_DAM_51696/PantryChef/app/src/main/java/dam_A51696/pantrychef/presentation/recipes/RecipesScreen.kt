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


/**
 * Ecrã base das receitas sugeridas
 *
 * Recebe o estado do viewmodel e encaminha a lógica visual correta
 * consoante a app esteja a carregar, dê erro ou tenha sucesso nos resultados
 *
 * @param onNavigateToRecipe função ativada para navegar para os detalhes de uma receita específica
 * @param onNavigateToIngredientViewMore função para saltar para a grelha completa de receitas de um só ingrediente
 * @param onNavigateToSearch função acionada ao clicar na lupa para abrir o ecrã manual de pesquisa
 * @param viewModel viewmodel injetado que liga a UI à camada de dados e lida com as operações lógicas
 */
@Composable
fun RecipesScreen(
    onNavigateToRecipe: (String) -> Unit,
    onNavigateToIngredientViewMore: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: RecipesViewModel = hiltViewModel()
) {
    // fica de olho no estado atual (ex: Success, Error, Loading)
    val uiState by viewModel.uiState.collectAsState()
    // lê o estado que diz quais categorias do menu "Discover More" estão fechadas
    val collapsedCategories by viewModel.collapsedCategories.collectAsState()

    // fundo principal do ecrã com a cor creme que definimos
    Scaffold(
        containerColor = CreamBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // avalia o tipo de estado que recebemos do ViewModel
            when (val state = uiState) {
                is RecipesUiState.Loading -> {
                    // se estiver a carregar a API, desenha uma rodinha ao centro
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ForestGreen)
                }
                is RecipesUiState.Error -> {
                    // se falhar a internet ou afins, atira a mensagem de erro para o ecrã a vermelho
                    Text(text = "Error: ${state.message}", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                is RecipesUiState.Success -> {
                    // se houver sucesso, chama finalmente a função gigante que desenha os elementos reais
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

/**
 * Interface principal das receitas quando o carregamento é concluído
 * Agrega a receira prioritária ("Best Match") e todas as categorias por ingrediente no menu
 *
 * @param bestMatch receita sugerida prioritária porque o ingrediente vai expirar (pode ser null)
 * @param bestMatchUsedIngredients lista de ingredientes da nossa despensa que estão a ser usados no "best match"
 * @param groupedRecipes lista chave-valor que agrupa cada nome de ingrediente à sua lista de receitas recomendadas
 * @param noRecipeIngredients ingredientes da despensa urgentes para os quais a API devolveu 0 resultados
 * @param collapsedCategories as categorias que o utilizador escolheu encolher clicando na seta
 * @param onToggleCategory evento acionado ao clicar no título ou seta de uma categoria
 * @param onNavigateToSearch ativada na lupa de pesquisa
 * @param onNavigateToRecipe ativada num cartão individual
 * @param onNavigateToIngredientViewMore ativada ao clicar no "View More" para ver mais de 6
 * @param paddingValues calcula as margens das barras de sistema
 */
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

    // a lista infinita onde deitamos o resto das funcionalidades
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        // usa-se o padding system no topo e em baixo para não haver elementos cobertos pela câmara
        contentPadding = PaddingValues(
            start = 24.dp, 
            end = 24.dp,
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding()
        )
    ) {
        item {
            // zona do topo com o grande título e o ícone de pesquisa
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
                // botão redondo branco da lupa
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

        // verifica se há algum ingrediente para o qual a api não tem receitas
        if (noRecipeIngredients.isNotEmpty()) {
            item {
                // mostra a caixa cor de laranja de perigo com essa informação
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
                        // faz uma string com vírgulas de todos os ingredientes
                        text = "We couldn't find recipes for: ${noRecipeIngredients.joinToString(", ")}",
                        color = ForestGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // se de facto a app conseguiu apurar um "melhor candidato", desenha a zona toda
        if (bestMatch != null) {
            item {
                // mini cabeçalho antes do best match
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
                // renderiza o cartão grande e envia a lógica visual lá para dentro
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
            // título de base fixo no separador de descobertas
            Text(
                "DISCOVER MORE",
                color = GrayText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // começa as listagens de todos os grupos de receitas obtidos
        if (groupedRecipes.isNotEmpty()) {
            // por cada ingrediente e a sua data source de receitas associadas
            groupedRecipes.forEach { (ingredientName, recipesList) ->
                // boolean gerado por verificar se este ingrediente existe no Set de nomes escondidos
                val isExpanded = !collapsedCategories.contains(ingredientName)
                item {
                    // cabeçalho interativo que esconde a lista (clicável na linha inteira)
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
                        // mostra seta para cima se tiver aberto, ou baixo se tiver escondido
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand/Collapse",
                            tint = GrayText
                        )
                    }
                }

                // se não tiver fechado no menu, calcula o corpo
                if (isExpanded) {
                    val recipesToShow = recipesList.take(6) // mostra um máximo de 6 receitas nesta aba
                    val pairs = recipesToShow.chunked(2) // divide para grelha em pares

                    // loop pelo tamanho dos pares
                    items(pairs.size) { index ->
                        val pair = pairs[index]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // primeiro da linha
                            RecipeGridCard(
                                recipe = pair[0],
                                modifier = Modifier.weight(1f), // o weight dá força igual
                                // na margem e tamanho a ambas as partes
                                onClick = { onNavigateToRecipe(pair[0].idMeal) }
                            )
                            // como se dividiu em 2, é preciso garantir que a linha tem
                            // uma segunda receita
                            if (pair.size > 1) {
                                RecipeGridCard(
                                    recipe = pair[1],
                                    modifier = Modifier.weight(1f),
                                    onClick = { onNavigateToRecipe(pair[1].idMeal) }
                                )
                            } else {
                                // se houver uma receita impar e isolada
                                // (ex. só temos 5 receitas de batata, e este é o fim)
                                // põe-se um espaço falso
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // se existirem mais do que 6 receitas, aparece o botão View More
                    if (recipesList.size > 6) {
                        item {
                            // gera o botão textual de View More
                            Text(
                                text = "View More",
                                color = PrimaryOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    // navega e passa o ingrediente à próxima viewmodel
                                    .clickable { onNavigateToIngredientViewMore(ingredientName) }
                                    .padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        } else {
            // se de facto a despensa não tem nada ou os ingredientes não serviram
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

/**
 * Componente do cartão que salienta a recomendação principal (Best Match) baseada nos prazos
 *
 * @param recipe objeto de onde sacamos o thumbnail grande
 * @param usedIngredients os nomes de despensa envolvidos na preparação para listar nas letras pequenas
 * @param onClick evento ativado num toque qualquer na área do cartão
 */
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

/**
 * O pequeno módulo individual com formato padrão para integrar nas listas extensas
 *
 * @param recipe contém apenas o thumbnail pequeno e o seu nome respetivo para usar
 * @param modifier permite aplicar alterações superiores na função mãe a este design
 * @param onClick a ordem de ativação enviada se a pressão o alcançar
 */
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

/**
 * Criei este ficheiro para fazer o ecrã principal das receitas (Recipes)
 *
 * Serve para recomendar receitas automáticas baseadas nos ingredientes da despensa
 * e agrupar essas receitas em listas dinâmicas expansíveis
 *
 * Funções e componentes criados:
 * - RecipesScreen:
 *      É a função de entrada. Fica à escuta do RecipesUiState do ViewModel e gere
 *      qual é o ecrã ou aviso a mostrar à frente
 * - RecipesContent:
 *      É o corpo da aplicação para as receitas. Cria uma LazyColumn infinita e mostra o título,
 *      o botão de pesquisa, os avisos (caso não encontre receitas), o grande destaque (BestMatchCard)
 *      e as categorias abertas/fechadas com as respetivas grelhas
 * - BestMatchCard:
 *      É o cartão visual gigante, com cantos arredondados, feito para destacar uma
 *      sugestão urgente. Mostra a etiqueta verde de destaque, e corta a imagem
 *      perfeitamente usando ContentScale.Crop
 * - RecipeGridCard:
 *      É a pequena célula reutilizável com uma imagem e título que preenche as
 *      linhas da grelha de forma simétrica
 */
