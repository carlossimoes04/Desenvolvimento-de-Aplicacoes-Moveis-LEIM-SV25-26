package dam_A51696.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FavoritesSection(
    favorites: List<FavoriteLocation>, // lista de locais favoritos guardados
    onAddFavorite: (String) -> Unit, // callback chamado quando o utilizador adiciona um local favorito
    onFavoriteClick: (FavoriteLocation) -> Unit // callback chamado quando o utilizador seleciona um local favorito
) {
    var showDialog by remember { mutableStateOf(false) } // controla se o diálogo de adicionar favorito está visível
    var newLocationName by remember { mutableStateOf("") } // guarda o nome escirto pelo utilizador no diálogo

    Column(modifier = Modifier.fillMaxWidth()) { // coluna que ocupa toda a largura disponível
        Row(
            modifier = Modifier.fillMaxWidth(), // linha que ocupa toda a largura disponível
            horizontalArrangement = Arrangement.SpaceBetween, // título à esquerda, botão de adicionar à direita
            verticalAlignment = Alignment.CenterVertically // alinha ao centro verticalmente
        ) {
            Text("Favorite Locations", fontWeight = FontWeight.Bold) // título da secção a negrito

            // botão de Adicionar Favorito
            IconButton(onClick = { showDialog = true }) { // ao clicar, mostra o diálogo de adicionar favorito
                Icon(Icons.Default.Add, contentDescription = "Add Favorite") // icon de "+"
            }
        }

        if (favorites.isEmpty()) { // se não houver favoritos guardados
            Text("No favorites saved yet.", style = MaterialTheme.typography.bodySmall) // mensagem de lista vazia
        } else {
            LazyRow( // lista horizontal com scroll para mostrar os favoritos
                horizontalArrangement = Arrangement.spacedBy(8.dp), // espaçamento de 8dp entre cada opção de local guardado
                contentPadding = PaddingValues(vertical = 8.dp) // margem vertical de 8dp na lista
            ) {
                items(favorites) { favorite -> // itera sobre cada favorito da lista
                    ElevatedFilterChip(
                        selected = false, // os favoritos nunca ficam marcados como selecionados
                        onClick = { onFavoriteClick(favorite) }, // ao clicar, seleciona este favorito e atualiza as coordenadas
                        label = { Text(favorite.name) }, // nome do local favorite
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) } // icon de localização à esquerda do nome
                    )
                }
            }
        }
    }

    if (showDialog) { // mostra o diálogo apenas quando showDialog for true
        AlertDialog(
            onDismissRequest = { showDialog = false }, // fecha o diálogo ao clicar fora dele
            title = { Text("Save Location") }, // título do diálogo
            text = {
                OutlinedTextField(
                    value = newLocationName, // valor atual do campo de texto
                    onValueChange = { newLocationName = it }, // atualiza o nome à medida que o utilizador escreve
                    label = { Text("Location Name (e.g., Home, Paris)") }, // etiqueta do campo
                    singleLine = true // impede que o campo expanda para múltiplas linhas
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newLocationName.isNotBlank()) { // só guarda se o nome não estiver vazio
                        onAddFavorite(newLocationName) // guarda o favorito com o nome escrito
                        newLocationName = "" // limpa o campo de texto para a próxima utilização
                        showDialog = false // fecha o diálogo
                    }
                }) {
                    Text("Save") // texto do botão de confirmar
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") } // fecha o diálogo sem guardar
            }
        )
    }
}