package dam_A51696.cooljetpackweatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import dam_A51696.cooljetpackweatherapp.data.WeatherApiClient
import dam_A51696.cooljetpackweatherapp.ui.WeatherUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WeatherViewModel : ViewModel(){

    // Estado da UI
    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

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
                val currentTime = weather.current_weather.time // string da hora atual
                val currentHour = currentTime.substringBefore(":") + ":00" // string da hora atual
                // currentHour funciona fazendo um corte no texto original exatamente onde estão os dois pontos
                // (deitando fora os minutos) e colando ":00" no final para forçar a hora a ficar certa
                val hourIndex = weather.hourly.time.indexOf(currentHour) // índice da hora atual na lista de horas
                val pressure = if (hourIndex >= 0) // calcula a pressão atmosférica
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
                        time = currentTime
                    )
                }
            }
        }
    }
}