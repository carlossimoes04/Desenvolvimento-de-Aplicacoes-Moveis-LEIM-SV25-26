package dam_A51696.pantrychef.presentation.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.GrayText
import dam_A51696.pantrychef.presentation.theme.TagGreen
import dam_A51696.pantrychef.presentation.theme.White
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import dam_A51696.pantrychef.domain.model.ShoppingItem


/**
 * Ecrã principal da lista de compras
 *
 * É aqui que a pessoa vê o que falta comprar para as receitas,
 * pode adicionar ingredientes novos ou simplesmente marcá-los como já postos no carrinho
 *
 * @param viewModel viewmodel associado ao ecrã injetado pelo hilt para lidarmos
 * com as funções de backend
 */
@Composable
fun ShoppingListScreen(
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    // fica a ouvir a lista toda diretamente da base de dados (são todos os itens misturados)
    val items by viewModel.items.collectAsState()

    // filtro rápido só para saber os itens que ainda não foram marcados
    val toBuy = items.filter { !it.isBought }
    // filtro para ter apenas os que estão marcados (para meter no fundo do ecrã)
    val recentlyBought = items.filter { it.isBought }

    // estado que só serve para guardar o texto que é escrito na caixa de texto
    var newItemName by remember { mutableStateOf("") }

    Scaffold(
        containerColor = CreamBackground
    ) { padding ->
        // uso da lista preguiçosa porque as listas de compras costumam ficar compridas
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            // afastar um bocado das margens do telemóvel
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            )
        ) {
            // parte de cima fixa (título, subtítulo e caixa de texto para adicionar mais itens)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        // título grande
                        Text(
                            text = "Shopping List",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        // texto que atualiza dinamicamente conforme falte X ingredientes
                        Text(
                            text = "${toBuy.size} items needed",
                            fontSize = 14.sp,
                            color = GrayText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                // input manual para adicionar um item novo à lista de compras
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Add custom item...", color = Color.Black) },
                    leadingIcon = {
                        // botão de "adicionar" ao lado da caixa
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier
                                .border(1.dp, ForestGreen, CircleShape)
                                .padding(2.dp)
                                .size(16.dp)
                                .clickable
                                {
                                // chama a ação de adicionar e envia com a string que temos agora
                                viewModel.addItem(newItemName, "Manual entry")
                                newItemName = ""
                            }
                        )
                    },
                    // isto faz com que o botão de "Enter" do teclado seja um "Check"
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // a mesma coisa que o botão de cima:
                            // permite fechar usando o teclado
                            viewModel.addItem(newItemName, "Manual entry")
                            newItemName = ""
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        // tiro aquela linha nojenta por baixo da textbox do android
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))

                // secção de itens que ainda não foram marcados
                Text(
                    text = "TO BUY",
                    color = GrayText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // ciclo que desenha os ingredientes que vieram do primeiro filtro (toBuy)
            items(toBuy.size) { index ->
                ShoppingItemCard(
                    item = toBuy[index],
                    // ação de marcar como comprado
                    onToggle = { viewModel.toggleItem(toBuy[index]) },
                    // ação de remover
                    onDelete = { viewModel.deleteItem(toBuy[index]) }
                )
            }

            // se já se marcou o item como comprado
            if (recentlyBought.isNotEmpty()) {
                item {
                    // separador entre as duas secções
                    Spacer(modifier = Modifier.height(32.dp))
                    // secção de itens que foram marcados
                    Text(
                        text = "RECENTLY BOUGHT",
                        color = GrayText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // ciclo que desenha os ingredientes que vieram do segundo filtro (recentlyBought)
                items(recentlyBought.size) { index ->
                    BoughtItemCard(
                        item = recentlyBought[index],
                        // ação de marcar como não comprado
                        onToggle = { viewModel.toggleItem(recentlyBought[index]) },
                        // ação de remover
                        onDelete = { viewModel.deleteItem(recentlyBought[index]) }
                    )
                }
            }
        }
    }
}

/**
 * Cartão desenhado para os ingredientes que ainda nos faltam comprar
 *
 * Tem fundo branco forte e sombra, mais o espaço na esquerda
 *
 * @param item o item da lista de compras
 * @param onToggle função disparada no clique que pede ao viewmodel para atualizar a compra
 * @param onDelete função atirada no caixote do lixo para não se ver mais
 */
@Composable
fun ShoppingItemCard(item: ShoppingItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() } // permite o clique em qualquer ponto do cartão
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // botão da bolinha vazio pronto a marcar
            IconButton(onClick = onToggle, modifier = Modifier.size(24.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, TagGreen, CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                // nome do item e detalhes
                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(text = item.details, color = GrayText, fontSize = 12.sp)
            }
            // botão de lixo vermelho
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Cartão desenhado para os itens que foram marcados como "comprados"
 *
 * @param item o objeto correspondente da bd que tem o bool "isBought = true"
 * @param onToggle função que volta a torná-lo "not bought" mandando-o para a zona de cima
 * @param onDelete função que apaga o item da lista
 */
@Composable
fun BoughtItemCard(item: ShoppingItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        // coloca-se o cartão da mesma cor que a background
        colors = CardDefaults.cardColors(containerColor = CreamBackground),
        // tirar a sombra do cartão
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // bola pintada de verde com check
            IconButton(onClick = onToggle, modifier = Modifier.size(24.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TagGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                // nome do item com texto riscado
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GrayText,
                    textDecoration = TextDecoration.LineThrough
                )
                Text(text = item.details, color = Color.LightGray, fontSize = 12.sp)
            }
            // botão de lixo vermelho
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Criei este ficheiro para desenhar a lista de compras da app (ShoppingListScreen)
 *
 * Serve para poder controlar tudo o que falta para fazer receitas,
 * dividindo entre coisas por comprar (To Buy) e coisas que já foram compradas
 * (Recently Bought)
 *
 * Funções e componentes criados:
 * - ShoppingListScreen:
 *      É a função mestre da lista de compras. Puxa os dados do ShoppingViewModel,
 *      divide as listas e trata de toda a estrutura: a cor de base bege (Scaffold),
 *      o cabeçalho com a contagem de itens, a caixa de adicionar ingredientes
 *      manuais (OutlinedTextField) e a organização das listas numa LazyColumn
 * - ShoppingItemCard:
 *      É a célula de UI com cantos arredondados que usamos para cada ingrediente
 *      urgente "To Buy". Tem fundo branco com sombra (para destacar), uma bolinha
 *      de seleção verde à esquerda e um caixote de lixo vermelho
 * - BoughtItemCard:
 *      O mesmo tipo de cartão mas para a secção inferior de coisas finalizadas
 *      ("Recently Bought"). Para dar um aspeto mais resolvido, a UI tira a sombra, funde
 *      a cor com a base do ecrã, risca os nomes e adiciona o check branco
 */
