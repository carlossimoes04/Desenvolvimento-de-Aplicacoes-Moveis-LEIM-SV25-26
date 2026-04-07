package dam_A51696.coolweatherapp

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

// adicionados devido à challenge 2.2.1, de modo a poder suportar o GPS nativo do android
import android.location.LocationManager
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider

// adicionados devido à challenge 2.2.2
import dam_A51696.coolweatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // ViewBinding: substitui os findViewById
    private lateinit var binding: ActivityMainBinding

    /**
     * ViewModel: contém a lógica de negócio
     * criado com ViewModelProvider para sobreviver a rotações de ecrã
     */
    private lateinit var viewModel: WeatherViewModel

    private var day = true
    private var lastLat = 38.076f // coordenadas de fallback (Lisboa)
    private var lastLon = -9.12f // coordenadas de fallback (Lisboa)

    /**
     * gestor de localização nativo do Android
     * substitui a dependência externa FusedLocationProviderClient (Google Play Services)
     *
     * criado devido à challenge 2.2.1
     */
    private lateinit var locationManager: LocationManager

    /**
     * código de identificação do pedido de permissão de localização
     * usado em onRequestPermissionsResult para identificar a resposta do utilizador
     *
     * criado devido à challenge 2.2.1
     */
    private val LOCATION_PERMISSION_REQUEST = 1001

    // SharedPreferences
    private fun savePrefs() {
        getSharedPreferences("prefs", MODE_PRIVATE).edit()
            .putBoolean("day", day)
            .putFloat("lastLat", lastLat)
            .putFloat("lastLon", lastLon)
            .apply()
    }

    private fun loadPrefs() {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        day     = prefs.getBoolean("day", true)
        lastLat = prefs.getFloat("lastLat", 38.076f)
        lastLon = prefs.getFloat("lastLon", -9.12f)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        loadPrefs()

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

        // ViewBinding em vez de setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.container) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa o ViewModel
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        // Observa os dados meteorológicos (padrão Observer)
        viewModel.weatherData.observe(this) { weather ->
            updateUI(weather)
        }

        // Observa erros
        viewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }

        /**
         * inicializa o LocationManager através do sistema Android
         * anteriormente, o fetch era feito diretamente com coordenadas fixas
         */
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

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
        binding.gpsButton?.setOnClickListener {
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
     * View — atualiza a UI com os dados recebidos do ViewModel via LiveData.
     * Não contém lógica de negócio — apenas apresentação.
     */
    private fun updateUI(request: WeatherData) {
        val currentTime = request.current_weather.time.substringAfter("T")
        val sunrise     = request.daily.sunrise[0].substringAfter("T")
        val sunset      = request.daily.sunset[0].substringAfter("T")
        val newDay      = currentTime in sunrise..sunset

        if (newDay != day) {
            day = newDay
            savePrefs()
            recreate()
            return
        }

        binding.pressureValue.text    = "${request.hourly.pressure_msl[12]} hPa"
        binding.windDirectionValue.text = "${request.current_weather.winddirection}°"
        binding.windSpeedValue.text   = "${request.current_weather.windspeed} km/h"
        binding.temperatureValue.text = "${request.current_weather.temperature} ºC"
        binding.timeValue.text        = request.current_weather.time

        val wImage = getWeatherImage(request.current_weather.weathercode)
        val resID  = resources.getIdentifier(wImage, "drawable", packageName)
        if (resID != 0) {
            binding.weatherImage.setImageDrawable(getDrawable(resID))
        }
    }

    private fun getWeatherImage(weatherCode: Int): String? {
        val codes  = resources.getIntArray(R.array.weather_codes)
        val images = resources.getStringArray(R.array.weather_images)
        val index  = codes.indexOfFirst { it == weatherCode }
        if (index == -1) return null
        val baseImage = images[index]
        val dayNightCodes = intArrayOf(0, 1, 2)
        return if (weatherCode in dayNightCodes) {
            baseImage + if (day) "day" else "night"
        } else {
            baseImage
        }
    }

    private fun getLocationAndFetch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchWeather(lastLat, lastLon)
            return
        }

        val cached = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (cached != null) {
            lastLat = cached.latitude.toFloat()
            lastLon = cached.longitude.toFloat()
            binding.latitudeValue.setText(lastLat.toString())
            binding.longitudeValue.setText(lastLon.toString())
            viewModel.fetchWeather(lastLat, lastLon)
        } else {
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
            android.os.Handler(mainLooper).postDelayed({
                if (binding.latitudeValue.text.isEmpty()) {
                    binding.latitudeValue.setText(lastLat.toString())
                    binding.longitudeValue.setText(lastLon.toString())
                    viewModel.fetchWeather(lastLat, lastLon)
                }
            }, 5000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndFetch()
        } else {
            viewModel.fetchWeather(lastLat, lastLon)
        }
    }
}