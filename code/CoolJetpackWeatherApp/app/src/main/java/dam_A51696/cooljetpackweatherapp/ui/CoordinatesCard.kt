package dam_A51696.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
// imports necessários para o mapa
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.ui.Alignment
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import dam_A51696.cooljetpackweatherapp.R

@Composable
fun CoordinatesCard(
    latitude: Float,
    longitude: Float,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // latitude e longitude
    // [remember] permite que o valor seja preservado entre recomposições da atividade (updates da UI)
    // [mutableStateOf] guarda o valor da localização e avisa o Compose sempre que esse valor muda
    var latitudeText by remember(latitude) { mutableStateOf(latitude.toString()) }
    var longitudeText by remember(longitude) { mutableStateOf(longitude.toString()) }

    val context = LocalContext.current // obtém o ambiente atual da app

    val mapLauncher = rememberLauncherForActivityResult( // [rememberLauncherForActivityResult] permite abrir outra atividade e receber o resultado de volta
        contract = ActivityResultContracts.StartActivityForResult(),
        // [contract] define as regras da comunicação entre duas atividades
        // [ActivityResultContracts.StartActivityForResult] é uma regra que permite abrir uma atividade com um [Intent] e receber o resultado de volta
        onResult = { result -> // recebe o resultado de volta
        if (result.resultCode == Activity.RESULT_OK) { // se for RESULT_OK, isto é, se o utilizador confirmou a localização
            val newLat = result.data?.getFloatExtra("LATITUDE", latitude) ?: latitude // obtém a nova latitude
            val newLon = result.data?.getFloatExtra("LONGITUDE", longitude) ?: longitude // obtém a nova longitude

            // atualiza as variáveis de texto da latitude e da longitude
            latitudeText = newLat.toString()
            longitudeText = newLon.toString()

            // atualiza as variáveis de localização
            onLatitudeChange(latitudeText)
            onLongitudeChange(longitudeText)
            }
        }
    )

    Card(
        modifier = modifier.fillMaxWidth(), // o card ocupa toda a largura disponível
        shape = RoundedCornerShape(12.dp), // cantos arredondados com raio de 12dp
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // sombra de 4dp para dar profundidade ao card
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // coluna com margem interna de 16dp em todos os lados
            Row(
                modifier = Modifier.fillMaxWidth(), // a row ocupa toda a largura
                horizontalArrangement = Arrangement.SpaceBetween, // coloca os elementos nas extremidades opostas (título à esquerda, ícone à direita)
                verticalAlignment = Alignment.CenterVertically // alinha os elementos ao centro verticalmente
            ) {
                Text(
                    text = stringResource(id = R.string.coordinates_title), // procura o título ao strings.xml
                    fontWeight = FontWeight.Bold, // texto a negrito
                    fontSize = 16.sp // tamanho de 16sp
                )
                Icon(
                    imageVector = Icons.Default.Public, // ícone de globo
                    contentDescription = stringResource(id = R.string.pick_desc), // descrição
                    tint = MaterialTheme.colorScheme.primary, // cor do ícone igual à cor primária do tema da app
                    modifier = Modifier
                        .size(28.dp) // tamanho do ícone de 28dp
                        .clickable {
                            /*
                            intent explícito, isto é, sabe exatamente qual a Activity que vai abrir

                            um intent é um objeto do android que serve para comunicar entre componentes da app,
                            podendo transportar dados e abrir outras atividades
                            */
                            val intent = Intent(context, LocationPickerActivity::class.java)
                            mapLauncher.launch(intent) // lança a Activity do mapa
                        }
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // espaço vazio de 8dp entre o título e o primeiro campo
            OutlinedTextField(
                value = latitudeText, // valor atual do input de latitude
                onValueChange = { // chamado sempre que o utilizador escreve algo
                    latitudeText = it // atualiza o estado local
                    onLatitudeChange(it) // notifica o componente pai com o novo valor
                },
                label = { Text(stringResource(id = R.string.latitude_label)) }, // etiqueta do campo
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), // teclado numérico com vírgula decimal
                modifier = Modifier.fillMaxWidth(), // campo ocupa toda a largura
                singleLine = true // impede que o campo expanda para múltiplas linhas
            )
            Spacer(modifier = Modifier.height(8.dp)) // espaço vazio de 8dp entre os dois campos
            OutlinedTextField(
                value = longitudeText, // valor atual do input de longitude
                onValueChange = { // chamado sempre que o utilizador escreve algo
                    longitudeText = it // atualiza o estado local
                    onLongitudeChange(it) // notifica o componente pai com o novo valor
                },
                label = { Text(stringResource(id = R.string.longitude_label)) }, // etiqueta do campo
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), // teclado numérico com vírgula decimal
                modifier = Modifier.fillMaxWidth(), // campo ocupa toda a largura
                singleLine = true // impede que o campo expanda para múltiplas linhas
            )
        }
    }
}