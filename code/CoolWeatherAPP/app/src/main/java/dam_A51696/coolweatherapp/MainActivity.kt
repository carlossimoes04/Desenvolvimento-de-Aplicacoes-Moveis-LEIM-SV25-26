package dam_A51696.coolweatherapp

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// adicionados devido à challenge 2.2.1, de modo a poder suportar o GPS nativo do android
import android.location.LocationManager
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider

// adicionados devido à challenge 2.2.2
import dam_A51696.coolweatherapp.databinding.ActivityMainBinding

/**
 * [MainActivity] atua como a camada "View" no padrão de arquitetura MVVM
 *
 * A sua principal responsabilidade é observar as alterações de dados provenientes do ViewModel
 * e atualizar a interface de utilizador (UI) em conformidade, garantindo a separação
 * entre a lógica e a apresentação
 */
class MainActivity : AppCompatActivity() {

    /**
     * Instância de [ActivityMainBinding] para acesso seguro e direto aos elementos da UI,
     * substituindo a necessidade de usar findViewById repetidamente
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * [WeatherViewModel] contém toda a lógica de negócio e os dados da aplicação
     *
     * É instanciado através do ViewModelProvider para garantir que os dados sobrevivem
     * a mudanças de configuração (como a rotação do ecrã)
     */
    private lateinit var viewModel: WeatherViewModel

    // Variáveis de estado da UI e de localização (Lisboa como fallback default)
    private var day = true
    private var lastLat = 38.076f
    private var lastLon = -9.12f

    /**
     * Gestor nativo do Android para aceder aos serviços de localização do dispositivo
     *
     * Utilizado para cumprir o requisito de apresentação das coordenadas reais do utilizador
     * logo no arranque da aplicação
     */
    private lateinit var locationManager: LocationManager

    /**
     * Código identificador para o pedido de permissões de localização (Runtime Permissions)
     */
    private val LOCATION_PERMISSION_REQUEST = 1001

    /**
     * Guarda as preferências do utilizador (estado do dia e últimas coordenadas válidas)
     * no armazenamento local do dispositivo
     */
    private fun savePrefs() {
        getSharedPreferences("prefs", MODE_PRIVATE).edit()
            .putBoolean("day", day)
            .putFloat("lastLat", lastLat)
            .putFloat("lastLon", lastLon)
            .apply()
    }

    /**
     * Carrega as preferências guardadas anteriormente
     *
     * Se não existirem, assume os valores padrão (Lisboa e tema de dia)
     */
    private fun loadPrefs() {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        day     = prefs.getBoolean("day", true)
        lastLat = prefs.getFloat("lastLat", 38.076f)
        lastLon = prefs.getFloat("lastLon", -9.12f)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        loadPrefs()

        // Configuração dinâmica do tema (Light/Dark) com base na orientação e na variável 'day'
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                if (day) setTheme(R.style.Theme_Day)
                else setTheme(R.style.Theme_Night)
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                if (day) setTheme(R.style.Theme_Day_Land)
                else setTheme(R.style.Theme_Night_Land)
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicialização do ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.container) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa o ViewModel ligando-o ao ciclo de vida desta Activity
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        // Padrão Observer: A View "escuta" ativamente as alterações no LiveData do ViewModel
        viewModel.weatherData.observe(this) { weather ->
            updateUI(weather)
        }

        // Observa e apresenta mensagens de erro
        viewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }

        /**
         * inicializa o LocationManager através do sistema Android
         * anteriormente, o fetch era feito diretamente com coordenadas fixas
         */
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Verifica permissões de GPS no arranque
        // Se não as tiver, pede ao utilizador
        if (savedInstanceState == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST
                )
            } else {
                getLocationAndFetch()
            }
        }

        // Botão UPDATE
        binding.updateButton.setOnClickListener {
            val lat = binding.latitudeValue.text.toString().toFloatOrNull() ?: 38.076f
            val lon = binding.longitudeValue.text.toString().toFloatOrNull() ?: -9.12f
            lastLat = lat
            lastLon = lon
            viewModel.fetchWeather(lat, lon)
        }

        // Botão GPS
        binding.gpsButton.setOnClickListener {
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!gpsEnabled && !networkEnabled) {
                android.app.AlertDialog.Builder(this)
                    .setTitle("Localização desativada")
                    .setMessage("A localização está desativada. Ativa o GPS nas definições do dispositivo.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                getLocationAndFetch()
            }
        }
    }

    /**
     * Atualiza os elementos visuais do ecrã com os dados recebidos da API
     *
     * Também avalia se o tema da aplicação deve mudar (Dia vs Noite) com base
     * na hora atual comparada com as horas de nascer e pôr do sol locais
     *
     * @param request O objeto de dados [WeatherData] emitido pelo ViewModel
     */
    private fun updateUI(request: WeatherData) {
        val currentTime = request.current_weather.time.substringAfter("T")
        val sunrise = request.daily.sunrise[0].substringAfter("T")
        val sunset = request.daily.sunset[0].substringAfter("T")

        // Verifica se a hora atual local se encontra entre o nascer e o pôr do sol
        val newDay = currentTime in sunrise..sunset

        // Recria a Activity se houver transição entre o dia e a noite para aplicar o novo tema
        if (newDay != day) {
            day = newDay
            savePrefs()
            recreate()
            return
        }

        // Preenche os TextViews com os valores meteorológicos
        binding.pressureValue.text = "${request.hourly.pressure_msl[12]} hPa"
        binding.windDirectionValue.text = "${request.current_weather.winddirection}°"
        binding.windSpeedValue.text = "${request.current_weather.windspeed} km/h"
        binding.temperatureValue.text = "${request.current_weather.temperature} ºC"
        binding.timeValue.text = request.current_weather.time

        // Atualiza o ícone do estado do tempo
        val wImage = getWeatherImage(request.current_weather.weathercode)
        val resID = resources.getIdentifier(wImage, "drawable", packageName)
        if (resID != 0) {
            binding.weatherImage.setImageDrawable(getDrawable(resID))
        }
    }

    /**
     * Converte o código numérico meteorológico da API (WMO Weather Code) no nome
     *
     * do recurso visual correspondente, extraindo a informação de arrays XML (resources)
     * em vez de utilizar uma enumeração hardcoded
     *
     * * Adiciona o sufixo "day" ou "night" aos códigos relacionados com céu limpo ou nublado
     *
     * @param weatherCode O código inteiro retornado pela Open-Meteo API.
     * @return O nome do recurso `drawable` correspondente em formato String.
     */
    private fun getWeatherImage(weatherCode: Int): String? {
        val codes = resources.getIntArray(R.array.weather_codes)
        val images = resources.getStringArray(R.array.weather_images)
        val index = codes.indexOfFirst { it == weatherCode }
        if (index == -1) return null
        val baseImage = images[index]

        // Códigos 0, 1 e 2 representam céu limpo a parcialmente nublado e diferem entre dia e noite
        val dayNightCodes = intArrayOf(0, 1, 2)
        return if (weatherCode in dayNightCodes) {
            baseImage + if (day) "day" else "night"
        } else {
            baseImage
        }
    }

    /**
     * Tenta obter as coordenadas reais do dispositivo através do LocationManager
     *
     * Se conseguir obter a localização (em cache ou atualizada), preenche a UI
     * e aciona o ViewModel
     *
     * Caso contrário, faz fallback para a última localização conhecida
     */
    private fun getLocationAndFetch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchWeather(lastLat, lastLon)
            return
        }

        // Tenta obter a localização mais recente guardada em cache (Rápido)
        val cached = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (cached != null) {
            lastLat = cached.latitude.toFloat()
            lastLon = cached.longitude.toFloat()
            binding.latitudeValue.setText(lastLat.toString())
            binding.longitudeValue.setText(lastLon.toString())
            viewModel.fetchWeather(lastLat, lastLon)
        } else {
            // Se não houver cache, pede uma única atualização de localização ativamente (Lento)
            locationManager.requestSingleUpdate(
                LocationManager.NETWORK_PROVIDER,
                { location ->
                    lastLat = location.latitude.toFloat()
                    lastLon = location.longitude.toFloat()
                    runOnUiThread {
                        binding.latitudeValue.setText(lastLat.toString())
                        binding.longitudeValue.setText(lastLon.toString())
                    }
                    viewModel.fetchWeather(lastLat, lastLon)
                },
                null
            )

            // Timeout de 5 segundos
            // Se o GPS falhar em responder, usa as últimas coordenadas guardadas
            android.os.Handler(mainLooper).postDelayed({
                if (binding.latitudeValue.text.isEmpty()) {
                    binding.latitudeValue.setText(lastLat.toString())
                    binding.longitudeValue.setText(lastLon.toString())
                    viewModel.fetchWeather(lastLat, lastLon)
                }
            }, 5000)
        }
    }

    /**
     * Callback do sistema Android que recebe a resposta do utilizador ao pedido
     * de permissões de localização feito no ecrã inicial
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Se a permissão for concedida, procura o GPS; senão, avança com o fallback
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndFetch()
        } else {
            viewModel.fetchWeather(lastLat, lastLon)
        }
    }
}