package dam_A51696.cooljetpackweatherapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_A51696.cooljetpackweatherapp.data.FavoritesRepository
import kotlinx.coroutines.launch
import dam_A51696.cooljetpackweatherapp.data.WeatherApiClient
import dam_A51696.cooljetpackweatherapp.ui.FavoriteLocation
import dam_A51696.cooljetpackweatherapp.ui.WeatherUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WeatherViewModel(private val context: Context) : ViewModel(){

    // Estado da UI
    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    init {
        // Carrega os favoritos ao iniciar
        viewModelScope.launch {
            FavoritesRepository.loadFavorites(context).collect { saved ->
                _uiState.update { it.copy(favorites = saved) }
            }
        }
    }

    // Atualizar a Latitude
    fun updateLatitude(newLatitude: Float) {
        _uiState.update { currentState ->
            currentState.copy(latitude = newLatitude)
        }
    }

    // Atualizar a Longitude
    fun updateLongitude(newLongitude: Float) {
        _uiState.update { currentState ->
            currentState.copy(longitude = newLongitude)
        }
    }

    // Função chamada pela ‘interface’ para obter os dados da localização inserida pelo utilizador
    fun fetchWeather() {
        viewModelScope.launch{ // criação de uma corrotina que evita o bloqueio da thread atual
            val data = WeatherApiClient.getWeather( // faz o pedido à API e guarda a informação em data
                _uiState.value.latitude, // lê a latitude atual guardada no estado da UI
                _uiState.value.longitude // lê a longitude atual guardada no estado da UI
            )

            // o '?' verifica se os dados não são nulos, caso não sejam
            // o 'let' executa o código dentro das chavetas e chama ao objeto recebido 'weather'
            data?.let { weather ->
                // atenção: CurrentWeather é atualizada de 15 em 15 minutos
                val currentTime = weather.current_weather.time // string da hora atual
                // como a API fornece dados em horas certas, corta-se os minutos exatos
                // e força-se o ":00" (ex: "14:15" passa a "14:00") para a pesquisa
                val searchHour = currentTime.substringBefore(":") + ":00"
                // índice da hora arredondada a procurar na lista horária
                // de maneira a obter dados da sea level pressure
                val hourIndex = weather.hourly.time.indexOf(searchHour)
                val pressure = if (hourIndex >= 0) // procura a sea level pressure de acordo com a hora
                    // se encontrou a hora na lista
                    weather.hourly.pressure_msl[hourIndex].toFloat() // procura a pressão correspondente
                else 0f // se não encontrou a hora na lista, a pressão é 0 por segurança

                // inicia a atualização do estado da UI
                // '.update' é a forma segura de alterar um MutableStateFlow
                // dá acesso ao estado atual
                _uiState.update { currentState ->
                    currentState.copy(
                        // cria uma cópia exata do estado atual, substituindo apenas
                        // os valores metereológicos abaixo
                        temperature = weather.current_weather.temperature,
                        windspeed = weather.current_weather.windspeed,
                        winddirection = weather.current_weather.winddirection,
                        weathercode = weather.current_weather.weathercode,
                        seaLevelPressure = pressure,
                        time = currentTime,
                        isDay = weather.current_weather.is_day == 1
                    )
                }
            }
        }
    }

    fun addFavorite(name: String) { // adiciona um novo favorito à lista
        val currentLat = _uiState.value.latitude // lê a latitude atual
        val currentLon = _uiState.value.longitude // lê a longitude atual
        val newFavorite = FavoriteLocation(name, currentLat, currentLon) // cria um novo favorito
        val updated = _uiState.value.favorites + newFavorite // adiciona o novo favorito à lista

        _uiState.update { currentState -> // atualiza o estado da UI
            currentState.copy(favorites = updated) // substitui a lista de favoritos
        }

        viewModelScope.launch { // cria uma corrotina para guardar a lista de favoritos
            FavoritesRepository.saveFavorites(context, updated) // guarda a lista de favoritos
        }
    }

    fun selectFavorite(favorite: FavoriteLocation) { // ao selecionar uma localização favorita
        _uiState.update { currentState -> // atualiza o estado da UI
            currentState.copy( // cria uma cópia exata do estado atual
                latitude = favorite.latitude, // substitui a latitude pela latitude do favorito
                longitude = favorite.longitude // substitui a longitude pela longitude do favorito
            )
        }
        // chama a função fetchWeather() para obter os dados da metereologia do lugar selecionado à API
        fetchWeather()
    }
}