package dam_A51696.coolweatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

/**
 * ViewModel do padrão MVVM: contém toda a lógica de negócio
 * que estava anteriormente em MainActivity
 *
 * Comunica com a View (MainActivity) através de LiveData,
 * seguindo o padrão Observer
 */
class WeatherViewModel : ViewModel() {

    /**
     * LiveData que expõe os dados meteorológicos à View
     * MutableLiveData é privado, só o ViewModel pode alterar o valor
     * a View observa o weatherData público (read-only)
     */
    private val _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> = _weatherData

    /**
     * LiveData para comunicar erros à View
     */
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * faz o pedido à API Open-Meteo e publica o resultado no LiveData
     * corre numa Thread separada para não bloquear a UI
     *
     * @param lat Latitude
     * @param lon Longitude
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
                    // postValue é thread-safe, pode ser chamado fora da UI thread
                    _weatherData.postValue(data)
                }
            } catch (e: Exception) {
                _error.postValue("Erro ao obter dados: ${e.message}")
            }
        }.start()
    }
}