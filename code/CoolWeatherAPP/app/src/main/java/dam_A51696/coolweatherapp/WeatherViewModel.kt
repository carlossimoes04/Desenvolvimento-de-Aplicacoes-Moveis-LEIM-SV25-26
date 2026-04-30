package dam_A51696.coolweatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

/**
 * [WeatherViewModel] atua como a camada "ViewModel" no padrão de arquitetura MVVM
 * * **Como funciona no MVVM:**
 * 1. Ao herdar de [ViewModel], esta classe sobrevive às mudanças de
 * configuração do dispositivo (como rodar o ecrã). Se a MainActivity for destruída e
 * recriada, os dados aqui dentro mantêm-se intactos, evitando novos pedidos à API
 * 2. O ViewModel não sabe absolutamente nada sobre a View (MainActivity).
 * Ele não manipula TextViews nem Botões. O seu único trabalho é processar a lógica
 * de negócio e expor o estado (os dados) para quem quiser "ouvir"
 */
class WeatherViewModel : ViewModel() {

    /**
     * **Padrão de Encapsulamento (Backing Property) no MVVM:**
     * * `_weatherData` é do tipo [MutableLiveData]. Como é `private`, apenas este ViewModel
     * tem o "poder" de alterar o seu valor. Isto protege os dados de serem alterados
     * acidentalmente pela View
     */
    private val _weatherData = MutableLiveData<WeatherData>()

    /**
     * `weatherData` é do tipo [LiveData]. Como é público, a View (MainActivity) pode
     * aceder a esta variável, mas **apenas para observar** (read-only).
     * Sempre que o `_weatherData` for atualizado aqui dentro, o `weatherData` notifica
     * automaticamente a View para ela se redesenhar (Padrão Observer)
     */
    val weatherData: LiveData<WeatherData> = _weatherData

    /**
     * O mesmo padrão de encapsulamento aplicado para o tratamento de erros.
     *
     * Permite que a View seja notificada (ex: para mostrar um Toast) sem ter de
     * lidar com a lógica de tratamento das exceções (Try/Catch)
     */
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * Faz o pedido à API Open-Meteo com base nas coordenadas fornecidas
     * * **Como funciona o processamento assíncrono:**
     * No Android, a interface de utilizador corre na "Main Thread". Se se fizer
     * um pedido de rede nessa thread, a app bloqueia. Por isso, delegamos
     * este trabalho pesado para uma [Thread] secundária (Background)
     *
     * @param lat Latitude da localização pretendida
     * @param lon Longitude da localização pretendida
     */
    fun fetchWeather(lat: Float, lon: Float) {
        Thread {
            try {
                val reqString = buildString {
                    append("https://api.open-meteo.com/v1/forecast?")
                    append("latitude=${lat}&longitude=${lon}&")
                    append("current_weather=true&")
                    append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m&")
                    append("daily=sunrise,sunset&timezone=auto")
                }
                val url = URL(reqString)
                url.openStream().use {
                    val data = Gson().fromJson(
                        InputStreamReader(it, "UTF-8"),
                        WeatherData::class.java
                    )

                    /**
                     * Como se está numa Thread secundária, o Android proíbe a alteração
                     * direta dos dados da interface (utilizando `.value = data`)
                     * O método `.postValue(data)` é "thread-safe": ele pega no resultado
                     * e envia-o em segurança de volta para a Main Thread para atualizar
                     * o LiveData e, consequentemente, a interface
                     */
                    _weatherData.postValue(data)
                }
            } catch (e: Exception) {
                _error.postValue("Erro ao obter dados: ${e.message}")
            }
        }.start()
    }
}