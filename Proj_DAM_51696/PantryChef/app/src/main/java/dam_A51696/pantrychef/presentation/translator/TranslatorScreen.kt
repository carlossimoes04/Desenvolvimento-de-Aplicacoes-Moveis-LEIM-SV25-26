package dam_A51696.pantrychef.presentation.translator

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dam_A51696.pantrychef.domain.model.Recipe
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.White

/**
 * Ecrã do Tradutor com Inteligência Artificial
 *
 * Permite ao utilizador escolher uma das suas receitas favoritas e pedir a
 * um bot para a traduzir para qualquer idioma, mostrando a conversa
 * no formato clássico de balões de chat
 *
 * @param viewModel a viewmodel injetada pelo Hilt que trata da internet e dos dados
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen(
    viewModel: TranslatorViewModel = hiltViewModel()
) {
    // tira os dados da viewmodel e fica sempre à escuta se houver mudanças
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    // variáveis locais que guardam o que a pessoa escreveu e a receita que escolheu
    var languageInput by remember { mutableStateOf("") }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    val focusManager = LocalFocusManager.current

    // para podermos esconder o teclado após se enviar a mensagem
    Scaffold(containerColor = CreamBackground) { padding ->
        // coluna gigante que ocupa o ecrã todo e respeita as margens
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
                // tocar no ecrã = esconder o teclado
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            // título da página
            Text(
                text = "AI Translator",
                color = ForestGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )

            // subtítulo cinzento
            Text(
                text = "Translate your favorite meals",
                color = dam_A51696.pantrychef.presentation.theme.GrayText,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // verifica se não tem receitas para mostrar uma mensagem cinzenta
            if (favorites.isEmpty()) {
                Text("No favorite recipes found.", color = Color.Gray, fontSize = 12.sp)
            } else {
                Text("Select a Favorite Recipe:", color = ForestGreen, fontSize = 14.sp)
                // desenha uma lista a rolar para o lado para se poder escolher a receita
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favorites) { recipe ->
                        // o botão da receita que fica logo marcado se clicarmos nele
                        FilterChip(
                            selected = (recipe == selectedRecipe),
                            onClick = { selectedRecipe = recipe },
                            label = { Text(recipe.strMeal) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // a lista vertical onde aparecem as mensagens todas do chat
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    // vê se a mensagem foi enviada pelo utilizador ou pelo bot
                    val isUser = msg.role == "user"
                    // alinha à direita se for o utilizador, ou à esquerda se for o bot
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        // o balãozinho com cantos redondos
                        // (verde para utilizador, branco para o bot)
                        Surface(
                            color = if (isUser) ForestGreen else White,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = msg.content,
                                color = if (isUser) White else Color.Black,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
                // a rodinha verde que aparece no fundo do chat enquanto a internet está a pensar
                if (isLoading) {
                    item {
                        Box(modifier = Modifier
                            .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = ForestGreen,
                                modifier = Modifier
                                    .padding(16.dp
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // a zona inferior onde escrevemos a linguagem e clicamos em enviar
            Row(verticalAlignment = Alignment.CenterVertically) {
                // a caixa de texto redonda e branca
                OutlinedTextField(
                    value = languageInput,
                    onValueChange = { languageInput = it },
                    placeholder = { Text("Language (e.g. Portuguese)", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                // botão redondo verde com a setinha de enviar
                IconButton(
                    onClick = {
                        // só envia se a pessoa escreveu alguma coisa
                        // e escolheu uma receita na lista
                        if (languageInput.isNotBlank() && selectedRecipe != null) {
                            viewModel.translateRecipe(selectedRecipe!!, languageInput)
                            languageInput = "" // limpa a caixa
                            focusManager.clearFocus() // esconde o teclado
                        }
                    },
                    modifier = Modifier.background(ForestGreen, shape = RoundedCornerShape(100))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = White)
                }
            }
        }
    }
}

/*
 * Criei este ficheiro para ser a cara principal do assistente inteligente,
 * a funcionar de forma muito parecida a um WhatsApp ou ao ChatGPT
 *
 * Decisões de Implementação:
 * - LazyRow em cima de LazyColumn:
 *      Para a pessoa poder escolher a receita logo ali sem ter de saltar para outra página,
 *      coloquei uma lista de receitas a rolar para o lado (LazyRow) por cima da zona do chat
 * - Esconder Teclado Automaticamente:
 *      Usei o focusManager quer no botão de enviar, quer com um toque na zona vazia do ecrã
 *      (detectTapGestures). Isto impede que o teclado do telemóvel fique aberto a tapar
 *      as respostas do bot
 * - Design dos Balões de Chat:
 *      Usei a propriedade do papel ("isUser") para decidir sozinho o alinhamento e as cores
 *      dos balões. Se for o utilizador, o balão encosta à direita e fica verde com texto branco.
 *      Se for a resposta do bot, encosta à esquerda e fica branco com texto preto
 */