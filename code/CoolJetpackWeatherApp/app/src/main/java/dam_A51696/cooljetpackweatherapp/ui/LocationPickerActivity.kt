package dam_A51696.cooljetpackweatherapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import dam_A51696.cooljetpackweatherapp.R

class LocationPickerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // isto permite que app ocupe o ecrã todo, por baixo das barras do sistema

        setContent {
            /*
            [selectedLocation] é a localização que o utilizador irá selecionar a partir do marcador no mapa
            [remember] garante que o valor é preservado entre recomposições da atividade (updates da UI)
            [mutableStateOf] guarda o valor da localização selecionada e avisa o Compose sempre que esse valor muda
            */
            var selectedLocation by remember { mutableStateOf(LatLng(38.7223, -9.1393)) }

            /*
            [cameraPositionState] guarda a posição da câmara no mapa
            [rememberCameraPositionState] cria e memoriza o estado da câmara do mapa
            [CameraPosition] descreve o ponto de vista da câmara sobre o mapa, ou seja, neste caso,
            a posição ([selectedLocation]) e o zoom da câmara (10f)
             */
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(selectedLocation, 10f)
            }

            Column(modifier = Modifier
                .fillMaxSize() // altura e largura máximas
                .windowInsetsPadding(WindowInsets.navigationBars) // adiciona padding para a barra de navegação
            ) {
                GoogleMap( // compose container do mapa do google maps
                    modifier = Modifier.weight(1f), // ocupa o restante espaço disponível na coluna
                    cameraPositionState = cameraPositionState, // para ser possível controlar a câmara do mapa
                    onMapClick = { locationClicked -> // quando o utilizador clica no mapa
                        selectedLocation = locationClicked // atualiza a localização selecionada
                    }
                ) {
                    Marker( // pino vermelho que aparece no mapa
                        state = MarkerState(position = selectedLocation), // recompõe o pino sempre que a localização selecionada muda
                        title = stringResource(id = R.string.chosen_location) // título que aparece ao clicar no pino
                    )
                }
                Button(
                    onClick = { // ao clicar no botão de confirmar
                        val resultIntent = Intent().apply { // cria um intent vazio e insere as coordenadas selecionadas
                            putExtra("LATITUDE", selectedLocation.latitude.toFloat()) // adiciona a latitude ao Intent (convertida para Float)
                            putExtra("LONGITUDE", selectedLocation.longitude.toFloat()) // adiciona a longitude ao Intent (convertida para Float)
                        }

                        setResult(RESULT_OK, resultIntent) // devolve o intent com as coordenadas à atividade principal com o código de sucesso

                        finish() // fecha esta atividade e volta à atividade principal
                    },
                    modifier = Modifier
                        .fillMaxWidth() // o botão ocupa toda a largura do ecrã
                        .padding(16.dp) // margem de 16dp à volta do botão
                        .height(50.dp) // altura fixa de 50dp
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm_location_btn) // procura o texto do botão ao strings.xml
                    )
                }
            }
        }
    }

}