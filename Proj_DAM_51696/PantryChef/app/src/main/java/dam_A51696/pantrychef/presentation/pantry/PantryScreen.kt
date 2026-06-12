package dam_A51696.pantrychef.presentation.pantry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dam_A51696.pantrychef.domain.model.Ingredient
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.GrayText
import dam_A51696.pantrychef.presentation.theme.LightForestGreen
import dam_A51696.pantrychef.presentation.theme.LightOrange
import dam_A51696.pantrychef.presentation.theme.PrimaryOrange
import dam_A51696.pantrychef.presentation.theme.TagGreen
import dam_A51696.pantrychef.presentation.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Componente principal (ecrã) que apresenta a lista de ingredientes da despensa do utilizador
 *
 * Esta função observa o estado atualizado dos ingredientes a partir da [PantryViewModel],
 * categoriza os ingredientes pelo seu estado de validade (expirados, a expirar brevemente ou bons)
 * e exibe-os numa lista com um comportamento "edge-to-edge"
 *
 * @param viewModel A view model injetada via Hilt responsável pela lógica da despensa
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(viewModel: PantryViewModel = hiltViewModel()) {
    // recolhe a lista de ingredientes mais recente fornecida pelo viewmodel
    val ingredients by viewModel.ingredients.collectAsState()
    // estado que controla a visibilidade do diálogo para adicionar um ingrediente
    var showAddDialog by remember { mutableStateOf(false) }
    // estado que guarda o ingrediente a ser editado (se for nulo, o diálogo de edição não aparece)
    var ingredientToEdit by remember { mutableStateOf<Ingredient?>(null) }

    // obtém o tempo atual em milissegundos para realizar os cálculos das validades
    val currentTime = System.currentTimeMillis()
    // define a constante que representa a quantidade de milissegundos num dia
    val dayInMillis = 1000 * 60 * 60 * 24L

    // filtra os ingredientes expirados (diferença de dias menor que 0)
    val expired = ingredients.filter {
        ((it.expirationDate - currentTime) / dayInMillis).toInt() < 0
    }

    // filtra os ingredientes a expirar brevemente (hoje, amanhã ou nos próximos 3 dias)
    val expiringSoon = ingredients.filter {
        val days = ((it.expirationDate - currentTime) / dayInMillis).toInt()
        days in 0..3
    }

    // filtra os ingredientes que ainda têm uma data de validade alargada (4 ou mais dias)
    val goodToGo = ingredients.filter {
        val days = ((it.expirationDate - currentTime) / dayInMillis).toInt()
        days >= 4
    }

    // estrutura base do ecrã com uma cor de fundo personalizada e um botão flutuante
    Scaffold(
        containerColor = CreamBackground,
        floatingActionButton = {
            // botão flutuante posicionado no canto inferior direito para adicionar ingredientes
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreen,
                contentColor = White
            ) {
                // icon de adição
                Icon(Icons.Default.Add, contentDescription = "Add Ingredient")
            }
        }
    ) { padding ->
        // lista desenhada apenas quando visível, ocupando o ecrã inteiro
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            // aplica o padding para criar o efeito edge-to-edge sem tapar texto
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            )
        ) {
            // cabeçalho superior com título, subtítulo, icon de perfil e pesquisa
            item {
                // linha que distribui os elementos nos extremos opostos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // agrupa o título e o subtítulo na vertical
                    Column {
                        Text(
                            text = "My Pantry",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "${expiringSoon.size} items expiring soon",
                            fontSize = 14.sp,
                            color = GrayText
                        )
                    }
                    // círculo que simula a fotografia de perfil do utilizador
                    // de momento só aparece um círculo verde que diz "Me"
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        // texto de perfil
                        Text("Me", color = White)
                    }
                }
                // aplica um pequeno espaço vertical
                Spacer(modifier = Modifier.height(24.dp))

                // caixa de texto com cantos arredondados que serve como barra de pesquisa
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Ingredients...", color = GrayText) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = GrayText
                        ) },
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = ForestGreen
                    )
                )
                // espaçamento vertical abaixo da barra de pesquisa
                Spacer(modifier = Modifier.height(24.dp))
            }

            // verifica se existem ingredientes na lista de expirados
            if (expired.isNotEmpty()) {
                // se existirem, adiciona o título da categoria à lista
                item {
                    SectionHeader(title = "EXPIRED", icon = Icons.Default.Warning, color = Color.Red)
                }
                // desenha um cartão de ingrediente por cada elemento na lista de expirados
                items(expired) { ingredient ->
                    IngredientCard(
                        ingredient = ingredient,
                        tagText = formatExpiringDate(ingredient.expirationDate),
                        tagBgColor = Color(0xFFFFEBEB),
                        tagTextColor = Color.Red,
                        onEdit = { ingredientToEdit = ingredient },
                        onDelete = { viewModel.deleteIngredient(ingredient) }
                    )
                }
            }

            // verifica se existem ingredientes na lista de próximos a expirar
            if (expiringSoon.isNotEmpty()) {
                // se existirem, adiciona o título desta categoria à lista
                item {
                    SectionHeader(title = "EXPIRING SOON", icon = Icons.Default.Warning, color = PrimaryOrange)
                }
                // desenha os respetivos cartões de ingrediente
                items(expiringSoon) { ingredient ->
                    IngredientCard(
                        ingredient = ingredient,
                        tagText = formatExpiringDate(ingredient.expirationDate),
                        tagBgColor = LightOrange,
                        tagTextColor = PrimaryOrange,
                        onEdit = { ingredientToEdit = ingredient },
                        onDelete = { viewModel.deleteIngredient(ingredient) }
                    )
                }
            }

            // verifica se existem ingredientes válidos e frescos
            if (goodToGo.isNotEmpty()) {
                // adiciona o respetivo título separador
                item {
                    SectionHeader(title = "GOOD TO GO", icon = Icons.Default.CheckCircle, color = ForestGreen)
                }
                // constrói as vistas de cada um desses ingredientes
                items(goodToGo) { ingredient ->
                    IngredientCard(
                        ingredient = ingredient,
                        tagText = formatExpiringDate(ingredient.expirationDate),
                        tagBgColor = LightForestGreen,
                        tagTextColor = ForestGreen,
                        onEdit = { ingredientToEdit = ingredient },
                        onDelete = { viewModel.deleteIngredient(ingredient) }
                    )
                }
            }
        }
    }

    // apresenta a janela flutuante para adicionar ingredientes, caso o utilizador tenha clicado no botão
    if (showAddDialog) {
        AddIngredientDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, qty, unit, date ->
                // efetua a inserção do novo ingrediente através da viewmodel e fecha o diálogo
                viewModel.addIngredient(name, qty, unit, date)
                showAddDialog = false
            }
        )
    }

    // apresenta o diálogo de edição caso haja algum ingrediente selecionado para ser editado
    ingredientToEdit?.let { ingredient ->
        EditIngredientDialog(
            ingredient = ingredient,
            onDismiss = { ingredientToEdit = null },
            onUpdate = { name, qty, unit, date ->
                // cria uma cópia exata do ingrediente atualizando apenas as propriedades novas
                val updated = ingredient.copy(
                    name = name,
                    quantity = qty,
                    unit = unit,
                    expirationDate = date
                )
                // atualiza a base de dados via viewmodel e limpa o ingrediente para esconder o diálogo
                viewModel.updateIngredient(updated)
                ingredientToEdit = null
            }
        )
    }
}

/**
 * Cabeçalho de secção que exibe um pequeno ícone seguido de um título
 *
 * Utilizado para categorizar listas de conteúdos no ecrã
 *
 * @param title O título descritivo da secção
 * @param icon O ícone associado ao contexto (ex: alerta, sucesso)
 * @param color A cor principal a ser aplicada ao ícone
 */
@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    // alinha o ícone e o texto verticalmente ao centro da linha
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            bottom = 16.dp, top = 8.dp
        )
    ) {
        // o ícone da secção
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        // um pequeno espaço vazio para separar o ícone do texto
        Spacer(modifier = Modifier.width(8.dp))
        // o texto convertido com ligeiro espaçamento entre as letras
        Text(
            text = title,
            color = GrayText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
    }
}

/**
 * Cartão visual para a listagem individual de ingredientes, mostrando dados vitais
 * e botões rápidos para a edição e remoção
 *
 * @param ingredient A entidade que contêm todos os dados do ingrediente
 * @param tagText O texto pré-formatado da data de validade a apresentar na etiqueta de estado
 * @param tagBgColor A cor de fundo para o retângulo que aloja o tempo de validade
 * @param tagTextColor A cor da fonte do texto do tempo de validade
 * @param onEdit A função desencadeada quando o ícone de lápis é pressionado
 * @param onDelete A função ativada para apagar a entrada da base de dados local
 */
@Composable
fun IngredientCard(
    ingredient: Ingredient,
    tagText: String,
    tagBgColor: Color,
    tagTextColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // base visual do ingrediente, com sombra indireta (opcional) e margens entre si
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        // divide interiormente as áreas de dados: a parte esquerda
        // (detalhes) e direita (ações e validade)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // zona esquerda com imagem de placeholder, nome do ingrediente e unidades
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CreamBackground),
                    contentAlignment = Alignment.Center
                ) {
                    // interior simulado do ícone ou fotografia do ingrediente
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(tagTextColor))
                }
                // afasta ligeiramente o círculo do bloco de texto
                Spacer(modifier = Modifier.width(16.dp))
                // agrupa nome e formato (quantidade) numa pilha vertical
                Column {
                    // exibe o título em negrito
                    Text(text = ingredient.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    // informa os valores métricos ao utilizador
                    Text(text = "${ingredient.quantity} ${ingredient.unit}", color = GrayText, fontSize = 14.sp)
                }
            }
            // zona direita com indicador temporal, edição e lixo
            Column(horizontalAlignment = Alignment.End) {
                // desenha a etiqueta retangular com cantos suaves
                Surface(
                    color = tagBgColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    // preenche o texto com tempo formatado que sobra
                    Text(
                        text = tagText,
                        color = tagTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                // separa a etiqueta temporal da fila que alberga os botões
                Spacer(modifier = Modifier.height(8.dp))
                // alinha lado a lado os ícones dinâmicos de interação de registo
                Row {
                    // botão gráfico simples acionado pelo utilizador para retificar
                    Icon(
                        Icons.Default.Edit, 
                        contentDescription = "Edit", 
                        tint = GrayText,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onEdit() }
                    )
                    // afasta um bocadinho os ícones um do outro
                    Spacer(modifier = Modifier.width(16.dp))
                    // botão acionado para eliminar algum registo corrompido ou inexistente
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Delete", 
                        tint = Color.Red,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onDelete() }
                    )
                }
            }
        }
    }
}

/**
 * Função utilitária que calcula a diferença entre o tempo atual e a validade de um produto
 *
 * Converte um registo cronológico (em milissegundos) para uma frase de fácil leitura
 *
 * @param timestamp O valor longo registado com o ponto limite da vida do produto
 * @return Um [String] que espelha visualmente e em texto a urgência da expiração do ingrediente
 */
fun formatExpiringDate(timestamp: Long): String {
    // apura a variação métrica em milissegundos num intervalo entre as datas
    val diff = timestamp - System.currentTimeMillis()
    // traduz essa porção enorme de números diários divisíveis para blocos inteiros
    val days = (diff / (1000 * 60 * 60 * 24)).toInt()
    // elabora condicionalmente o resultado devolvido
    return when {
        days < 0 -> "Expired"
        days == 0 -> "Expires today"
        days == 1 -> "Expires tomorrow"
        days < 30 -> "In $days days"
        else -> "In ${days / 30} months"
    }
}

/**
 * Caixa de diálogo invocada para registar de raiz uma nova instância de um ingrediente
 * que fará parte do stock armazenado na aplicação do cliente
 *
 * @param onDismiss Transfere a lógica do pedido de recuo/cancelamento para um plano superior
 * @param onAdd Método encarregue de passar os parâmetros compilados com sucesso
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientDialog(onDismiss: () -> Unit, onAdd: (String, Double, String, Long) -> Unit) {
    // estados locais preenchidos ao serem inseridos pelo utilizador
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var dateStr by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf(false) }

    // painel de aviso que aparece em primeiro plano
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Ingredient", color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            // compila todos os campos interativos verticais numa sucessão
            Column {
                // input do nome do ingrediente
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Name") }, modifier = Modifier.fillMaxWidth()
                )
                // divide em duas frações equitativas o input de quantidade e unidade
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // input de quantidade
                    OutlinedTextField(
                        value = quantity, onValueChange = { quantity = it },
                        label = { Text("Quantity") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    // input de unidade
                    OutlinedTextField(
                        value = unit, onValueChange = { unit = it },
                        label = { Text("Unit (e.g. L, g)") }, modifier = Modifier.weight(1f)
                    )
                }

                // input de data de validade, aceita os formatos dd/MM/YYYY, dd/MM, MM/YYYY e YYYY
                OutlinedTextField(
                    value = dateStr, 
                    onValueChange = { 
                        dateStr = it
                        dateError = false 
                    },
                    label = { Text("Expiration (DD/MM/YYYY, DD/MM, MM/YYYY, YYYY)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = dateError
                )
                // caso haja erro no formato da data, mostra uma mensagem
                if (dateError) {
                    Text("Invalid date format", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            // botão de confirmação que executa a ação de adicionar
            Button(
                onClick = {
                    // obtém os valores dos campos
                    val qty = quantity.toDoubleOrNull() ?: 0.0
                    
                    var expirationMs: Long? = null

                    // padrões de datas
                    val patterns = listOf(
                        "dd/MM/yyyy",
                        "dd/MM",
                        "MM/yyyy",
                        "yyyy"
                    )

                    // percorre a lista de padrões e tenta converter a data
                    for (pattern in patterns) {
                        try {
                            // tenta converter a data com o padrão atual
                            val format = SimpleDateFormat(pattern, Locale.getDefault())
                            // permite apenas datas futuras
                            format.isLenient = false
                            val parsedDate = format.parse(dateStr)

                            if (parsedDate != null) { // se conseguiu converter a data
                                // se o formato foi o "dd/MM"
                                if (pattern == "dd/MM") {
                                    // obtém o ano atual
                                    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                                    // cria uma instância de calendário
                                    val calendar = java.util.Calendar.getInstance()
                                    // ajusta a data para o ano atual
                                    calendar.time = parsedDate
                                    // ajusta o ano para o ano atual
                                    calendar.set(java.util.Calendar.YEAR, currentYear)
                                    // guarda o tempo ajustado
                                    expirationMs = calendar.timeInMillis
                                } else {
                                    // caso contrário, guarda-se o tempo normal
                                    expirationMs = parsedDate.time
                                }
                                break // assim que encontra um que funcione, sai do ciclo
                            }
                        } catch (e: Exception) {
                            // se der erro, tenta o próximo formato
                        }
                    }

                    // se não conseguiu converter a data, mostra erro
                    if (expirationMs == null) {
                        dateError = true
                        return@Button
                    }

                    // se tudo estiver certo e os campos preenchidos, guarda
                    if (name.isNotBlank() && quantity.isNotBlank() && unit.isNotBlank()) {
                        // no AddIngredientDialog é onAdd(), no EditIngredientDialog é onUpdate()
                        onAdd(name, qty, unit, expirationMs)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            // elimina da memória local as modificações por parte do utilizador
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                Text("Cancel", color = Color.Black)
            }
        },
        containerColor = White
    )
}

/**
 * Caixa de diálogo similar, invocada com um propósito distinto, retificar parcelas de valores
 * já embutidos ou processados por um registo inicial para consertar erros ou adicionar validade
 *
 * @param ingredient O alvo original dos dados populados dentro dos campos de texto ao abrir
 * @param onDismiss Operação transitiva para esconder o elemento suspenso
 * @param onUpdate Propagador passivo que transmite a versão atualizada da matriz temporal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIngredientDialog(
    ingredient: Ingredient,
    onDismiss: () -> Unit, 
    onUpdate: (String, Double, String, Long) -> Unit
) {
    // obtém o nome, quantidade e unidade
    var name by remember { mutableStateOf(ingredient.name) }
    // ajusta a quantidade para um formato mais fácil de ler
    var quantity by remember { mutableStateOf(if (ingredient.quantity % 1.0 == 0.0) ingredient.quantity.toInt().toString() else ingredient.quantity.toString()) }
    var unit by remember { mutableStateOf(ingredient.unit) }

    // formata a data para o padrão dd/MM/yyyy
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    // guarda a data em uma string
    var dateStr by remember { mutableStateOf(format.format(Date(ingredient.expirationDate))) }
    // erro de data
    var dateError by remember { mutableStateOf(false) }

    // painel de aviso que aparece em primeiro plano
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Ingredient", color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            // empilha ordenadamente o grupo de instâncias da edição
            Column {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Name") }, modifier = Modifier.fillMaxWidth()
                )
                // divide por igual o input de quantidade e unidade
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity, onValueChange = { quantity = it },
                        label = { Text("Quantity") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = unit, onValueChange = { unit = it },
                        label = { Text("Unit (e.g. L, g)") }, modifier = Modifier.weight(1f)
                    )
                }

                // input de data de validade
                OutlinedTextField(
                    value = dateStr, 
                    onValueChange = { 
                        dateStr = it
                        // limpa o erro de data
                        dateError = false 
                    },
                    label = { Text("Expiration (DD/MM/YYYY, DD/MM, MM/YYYY, YYYY)") }, 
                    modifier = Modifier.fillMaxWidth(),
                    isError = dateError
                )
                // caso haja erro no formato da data, mostra uma mensagem
                if (dateError) {
                    Text("Invalid date format", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            // botão de confirmação que executa a ação de editar
            Button(
                onClick = {
                    // obtém os valores dos campos
                    val qty = quantity.toDoubleOrNull() ?: 0.0
                    
                    var expirationMs: Long? = null

                    val patterns = listOf(
                        "dd/MM/yyyy",
                        "dd/MM",
                        "MM/yyyy",
                        "yyyy"
                    )
                    
                    for (pattern in patterns) {
                        try {
                            val format = SimpleDateFormat(pattern, Locale.getDefault())
                            format.isLenient = false
                            val parsedDate = format.parse(dateStr)
                            
                            if (parsedDate != null) {
                                if (pattern == "dd/MM") {
                                    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                                    val calendar = java.util.Calendar.getInstance()
                                    calendar.time = parsedDate
                                    calendar.set(java.util.Calendar.YEAR, currentYear)
                                    expirationMs = calendar.timeInMillis
                                } else {
                                    expirationMs = parsedDate.time
                                }
                                break
                            }
                        } catch (e: Exception) {
                            // try next
                        }
                    }

                    if (expirationMs == null) {
                        dateError = true
                        return@Button
                    }

                    if (name.isNotBlank() && quantity.isNotBlank() && unit.isNotBlank()) {
                        onUpdate(name, qty, unit, expirationMs)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                Text("Cancel", color = Color.Black)
            }
        },
        containerColor = White
    )
}

/**
 * Criei este ficheiro para fazer o ecrã da despensa (Pantry)
 * Serve para listar, adicionar, editar e apagar os ingredientes da aplicação,
 * e separá-los pela data de validade
 *
 * Funções e componentes criados:
 * - PantryScreen:
 *      É a função principal. Lê os dados do PantryViewModel, separa os ingredientes
 *      em três listas (Expirados, A Expirar, Bons) e mostra tudo numa LazyColumn
 * - CategoryChip:
 *      É o botão oval que uso para fazer os filtros de categoria
 * - SectionHeader:
 *      É o texto e o ícone que uso para dividir as secções dos ingredientes
 * - IngredientCard:
 *      É o layout que mostra a informação de cada ingrediente, a data e os botões
 *      de editar e apagar
 * - formatExpiringDate:
 *      Função que converte a data de validade (em milissegundos) para uma string legível
 * - AddIngredientDialog / EditIngredientDialog:
 *      São as caixas de diálogo onde adiciono ou edito ingredientes. Têm a lógica para testar
 *      vários formatos de data e aceitar formatos curtos como dia/mês (assumindo o ano atual)
 */