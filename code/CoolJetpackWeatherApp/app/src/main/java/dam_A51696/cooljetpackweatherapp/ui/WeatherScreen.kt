package dam_A51696.cooljetpackweatherapp.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import dam_A51696.cooljetpackweatherapp.data.WMO_WeatherCode
import dam_A51696.cooljetpackweatherapp.data.getWeatherCodeMap
import dam_A51696.cooljetpackweatherapp.viewmodel.WeatherViewModel

// imports para a interface
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.ui.res.stringResource
import dam_A51696.cooljetpackweatherapp.R

@Composable
fun WeatherUI(weatherViewModel: WeatherViewModel = viewModel()) {
    val weatherUIState by weatherViewModel.uiState.collectAsState() // observa o estado da UI do ViewModel, recompõe sempre que muda
    val latitude = weatherUIState.latitude // latitude atual
    val longitude = weatherUIState.longitude // longitude atual
    val temperature = weatherUIState.temperature // temperatura atual
    val windSpeed = weatherUIState.windspeed // velocidade do vento atual
    val windDirection = weatherUIState.winddirection // direção do vento atual
    val weatherCode = weatherUIState.weathercode // código WMO do estado do tempo atual
    val seaLevelPressure = weatherUIState.seaLevelPressure // pressão ao nível do mar atual
    val time = weatherUIState.time // hora da última atualização
    val favorites = weatherUIState.favorites // lista de locais favoritos guardados

    val configuration = LocalConfiguration.current // configuração do dispositivo (orientação, tamanho do ecrã, etc.)

    val day = true // Must change this in the future
    val mapt = getWeatherCodeMap() // obtém o mapa que associa códigos WMO a informações do estado do tempo
    val wCode = mapt.get(weatherCode) // obtém o objeto WMO correspondente ao código atual
    val wImage = when (wCode) { // escolhe o nome da imagem consoante o estado do tempo e se é dia ou noite
        WMO_WeatherCode.CLEAR_SKY,
        WMO_WeatherCode.MAINLY_CLEAR,
        WMO_WeatherCode.PARTLY_CLOUDY -> if (day) wCode?.image + "day" // céu limpo/pouco nublado de dia
        else wCode?.image + "night" // céu limpo/pouco nublado de noite
        else -> wCode?.image // para todos os outros estados do tempo a imagem é a mesma de dia e de noite
    }

    val context = LocalContext.current // contexto necessário para aceder aos recursos da app
    val wIcon = context.resources.getIdentifier(wImage, "drawable", context.packageName) // obtém o ID do recurso drawable a partir do nome da imagem

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) { // se o dispositivo estiver em modo paisagem
        LandscapeWeatherUI(
            wIcon,
            latitude,
            longitude,
            temperature,
            windSpeed,
            windDirection,
            weatherCode,
            seaLevelPressure,
            time,
            onLatitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) } // atualiza a latitude no ViewModel se o valor for válido
            },
            onLongitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) } // atualiza a longitude no ViewModel se o valor for válido
            },
            onUpdateButtonClick = {
                weatherViewModel.fetchWeather() // obtém os dados meteorológicos com as coordenadas atuais
            },
            favorites = favorites,
            onAddFavorite = { nome -> weatherViewModel.addFavorite(nome) }, // adiciona um novo favorito com o nome dado
            onFavoriteClick = { local -> weatherViewModel.selectFavorite(local) } // seleciona um favorito e atualiza as coordenadas
        )
    } else { // se o dispositivo estiver em modo retrato
        PortraitWeatherUI(
            wIcon,
            latitude,
            longitude,
            temperature,
            windSpeed,
            windDirection,
            weatherCode,
            seaLevelPressure,
            time,
            onLatitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) } // atualiza a latitude no ViewModel se o valor for válido
            },
            onLongitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) } // atualiza a longitude no ViewModel se o valor for válido
            },
            onUpdateButtonClick = {
                weatherViewModel.fetchWeather() // obtém os dados meteorológicos com as coordenadas atuais
            },
            favorites = favorites,
            onAddFavorite = { nome -> weatherViewModel.addFavorite(nome) }, // adiciona um novo favorito com o nome dado
            onFavoriteClick = { local -> weatherViewModel.selectFavorite(local) } // seleciona um favorito e atualiza as coordenadas
        )
    }
}

@Composable
fun PortraitWeatherUI(
    wIcon: Int,
    latitude: Float,
    longitude: Float,
    temperature: Float,
    windSpeed: Float,
    windDirection: Float,
    weathercode: Int,
    seaLevelPressure: Float,
    time: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit,
    favorites: List<FavoriteLocation>,
    onAddFavorite: (String) -> Unit,
    onFavoriteClick: (FavoriteLocation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize() // ocupa toda a largura e altura disponíveis
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp), // margem interna: 48dp no topo, 16dp nos restantes lados
        horizontalAlignment = Alignment.CenterHorizontally, // alinha todos os elementos ao centro horizontalmente
        verticalArrangement = Arrangement.spacedBy(12.dp) // espaçamento automático de 12dp entre cada elemento
    ) {
        if (wIcon != 0) { // 0 significa que o recurso não foi encontrado, pelo que só mostra a imagem se for válida
            Image(
                painter = painterResource(id = wIcon), // carrega a imagem do drawable correspondente ao estado do tempo
                contentDescription = stringResource(id = R.string.weather_icon_desc),
                modifier = Modifier.size(120.dp) // tamanho fixo de 120dp
            )
        }

        CoordinatesCard(
            latitude = latitude, // latitude atual
            longitude = longitude, // longitude atual
            onLatitudeChange = onLatitudeChange, // callback chamado quando o utilizador altera a latitude
            onLongitudeChange = onLongitudeChange // callback chamado quando o utilizador altera a longitude
        )

        Spacer(modifier = Modifier.height(16.dp)) // espaço vazio de 16dp

        FavoritesSection(
            favorites = favorites, // lista dos locais favoritos
            onAddFavorite = onAddFavorite, // callback chamado quando o utilizador adiciona um local favorito
            onFavoriteClick = onFavoriteClick // callback chamado quando o utilizador seleciona um local favorito
        )

        Spacer(modifier = Modifier.height(16.dp)) // espaço vazio de 16dp

        WeatherCard(
            temperature = temperature, // temperatura atual
            windSpeed = windSpeed, // velocidade do vento atual
            windDirection = windDirection, // direção do vento atual
            seaLevelPressure = seaLevelPressure, // pressão ao nível do mar atual
            time = time // hora da última atualização
        )

        Button(
            onClick = onUpdateButtonClick, // callback chamado ao clicar: obtém os dados meteorológicos atualizados
            modifier = Modifier
                .fillMaxWidth() // o botão ocupa toda a largura disponível
                .height(50.dp), // altura fixa de 50dp
            shape = RoundedCornerShape(12.dp) // cantos arredondados com raio de 12dp
        ) {
            Text(text = stringResource(id = R.string.update_weather_btn), fontSize = 16.sp) // texto do botão com tamanho de 16sp
        }
    }
}

@Composable
fun LandscapeWeatherUI(
    wIcon: Int,
    latitude: Float,
    longitude: Float,
    temperature: Float,
    windSpeed: Float,
    windDirection: Float,
    weathercode: Int,
    seaLevelPressure: Float,
    time: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit,
    favorites: List<FavoriteLocation>,
    onAddFavorite: (String) -> Unit,
    onFavoriteClick: (FavoriteLocation) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            CoordinatesCard(
                latitude = latitude,
                longitude = longitude,
                onLatitudeChange = onLatitudeChange,
                onLongitudeChange = onLongitudeChange
            )

            FavoritesSection(
                favorites = favorites,
                onAddFavorite = onAddFavorite,
                onFavoriteClick = onFavoriteClick
            )

            Button(
                onClick = onUpdateButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(id = R.string.update_weather_btn), fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (wIcon != 0) {
                Image(
                    painter = painterResource(id = wIcon),
                    contentDescription = stringResource(id = R.string.weather_icon_desc),
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )
            }

            WeatherCard(
                temperature = temperature,
                windSpeed = windSpeed,
                windDirection = windDirection,
                seaLevelPressure = seaLevelPressure,
                time = time,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}