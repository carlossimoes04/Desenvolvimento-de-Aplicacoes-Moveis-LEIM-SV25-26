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
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

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

    private fun saveDay(value: Boolean) {
        getSharedPreferences("prefs", MODE_PRIVATE).edit().putBoolean("day", value).apply()
    }

    private fun loadDay(): Boolean {
        return getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("day", true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        day = loadDay()
        if (savedInstanceState != null) {
            lastLat = savedInstanceState.getFloat("lastLat", 38.076f)
            lastLon = savedInstanceState.getFloat("lastLon", -9.12f)
        }

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
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /**
         * inicializa o LocationManager através do sistema Android
         * anteriormente, o fetch era feito diretamente com coordenadas fixas
         */
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        /**
         * substitui o fetchWeatherData(lastLat, lastLon).start()
         * verifica primeiro se a app tem permissão de localização:
         *  - se não tem: pede permissão ao utilizador (popup do sistema)
         *  - se já tem: chama getLocationAndFetch() que obtém GPS antes do fetch
         */
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

        val updateButton = findViewById<Button>(R.id.updateButton)
        val latInput = findViewById<EditText>(R.id.latitudeValue)
        val lonInput = findViewById<EditText>(R.id.longitudeValue)

        updateButton.setOnClickListener {
            val lat = latInput.text.toString().toFloatOrNull() ?: 38.076f
            val lon = lonInput.text.toString().toFloatOrNull() ?: -9.12f
            lastLat = lat
            lastLon = lon
            fetchWeatherData(lat, lon).start()
        }

        val gpsButton = findViewById<Button>(R.id.gpsButton)

        gpsButton.setOnClickListener {
            // Verifica se o GPS/localização está ativado no sistema
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!gpsEnabled && !networkEnabled) {
                // GPS desligado - mostra popup e não faz nada
                android.app.AlertDialog.Builder(this)
                    .setTitle("Localização desativada")
                    .setMessage("A localização está desativada. Ativa o GPS nas definições do dispositivo.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                // GPS ligado - obtém localização e atualiza
                getLocationAndFetch()
            }
        }
    }

    // Guarda o day e coordenadas antes de um recreate
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat("lastLat", lastLat)
        outState.putFloat("lastLon", lastLon)
    }

    private fun WeatherAPI_Call(lat: Float ,long : Float) : WeatherData {
        val reqString = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${lat}&longitude=${long}&")
            append("current_weather=true&")
            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m&")
            append("daily=sunrise,sunset&timezone=auto")
        }
        val url = URL(reqString)
        url.openStream().use {
            return Gson().fromJson(InputStreamReader(it, "UTF-8"), WeatherData::class.java)
        }
    }

    private fun fetchWeatherData (lat: Float, long : Float) : Thread {
        return Thread {
            val weather = WeatherAPI_Call (lat , long)
            updateUI(weather)
        }
    }

    private fun updateUI ( request : WeatherData ) {
        runOnUiThread {

            val currentTime = request.current_weather.time.substringAfter("T") // ex: "14:00"
            val sunrise     = request.daily.sunrise[0].substringAfter("T")     // ex: "07:23"
            val sunset      = request.daily.sunset[0].substringAfter("T")      // ex: "19:45"
            val newDay      = currentTime in sunrise..sunset

            // Se o dia/noite mudou, guarda e reinicia a activity para aplicar o novo tema
            if (newDay != day) {
                day = newDay
                saveDay(day) // guarda antes do recreate
                recreate()
                return@runOnUiThread
            }

            val weatherImage : ImageView = findViewById(R.id.weatherImage)
            val pressure: TextView = findViewById(R.id.pressureValue)
            val windDir    = findViewById<TextView>(R.id.windDirectionValue)
            val windSpeed  = findViewById<TextView>(R.id.windSpeedValue)
            val temp       = findViewById<TextView>(R.id.temperatureValue)
            val time       = findViewById<TextView>(R.id.timeValue)

            pressure.text  = "${request.hourly.pressure_msl[12]} hPa"
            windDir.text   = "${request.current_weather.winddirection}°"
            windSpeed.text = "${request.current_weather.windspeed} km/h"
            temp.text      = "${request.current_weather.temperature} ºC"
            time.text      = request.current_weather.time

            /**
             * anteriormente usava o enum WMO_WeatherCode e getWeatherCodeMap()
             * definidos em WeatherData.kt para obter o nome da imagem
             *
             * agora chama getWeatherImage() que lê os arrays do ficheiro weather_codes.xml
             */
            val wImage = getWeatherImage(request.current_weather.weathercode)
            val resID = resources.getIdentifier(wImage, "drawable", packageName)
            if (resID != 0) {
                weatherImage.setImageDrawable(getDrawable(resID))
            }
        }
    }

    /**
     * exercício 2.2.1 - obtém a última localização conhecida do dispositivo e inicia o fetch
     *
     * Tenta primeiro o provider GPS (mais preciso), e em fallback o NETWORK (menos preciso)
     * Se nenhum devolver localização, mantém as coordenadas padrão de Lisboa definidas em
     * lastLat/lastLon
     */
    private fun getLocationAndFetch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            fetchWeatherData(lastLat, lastLon).start()
            return
        }

        val cached = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (cached != null) {
            // Tem cache — usa imediatamente
            lastLat = cached.latitude.toFloat()
            lastLon = cached.longitude.toFloat()
            findViewById<EditText>(R.id.latitudeValue).setText(lastLat.toString())
            findViewById<EditText>(R.id.longitudeValue).setText(lastLon.toString())
            fetchWeatherData(lastLat, lastLon).start()
        } else {
            // Sem cache — pede uma localização ao sistema e aguarda
            locationManager.requestSingleUpdate(
                LocationManager.NETWORK_PROVIDER,
                { location ->
                    lastLat = location.latitude.toFloat()
                    lastLon = location.longitude.toFloat()
                    runOnUiThread {
                        findViewById<EditText>(R.id.latitudeValue).setText(lastLat.toString())
                        findViewById<EditText>(R.id.longitudeValue).setText(lastLon.toString())
                    }
                    fetchWeatherData(lastLat, lastLon).start()
                },
                null
            )
            // Fallback após 5 segundos caso o GPS não responda
            android.os.Handler(mainLooper).postDelayed({
                if (findViewById<EditText>(R.id.latitudeValue).text.isEmpty()) {
                    findViewById<EditText>(R.id.latitudeValue).setText(lastLat.toString())
                    findViewById<EditText>(R.id.longitudeValue).setText(lastLon.toString())
                    fetchWeatherData(lastLat, lastLon).start()
                }
            }, 5000)
        }
    }

    /**
     * exercício 2.2.1 - callback chamada pelo sistema após o utilizador responder
     * ao pedido de permissão de localização
     *
     * - se aceitar: chama getLocationAndFetch() para usar o GPS
     * - se recusar: faz fetch com as coordenadas de fallback (Lisboa)
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndFetch()
        } else {
            // sem permissão: usa coordenadas padrão (Lisboa)
            fetchWeatherData(lastLat, lastLon).start()
        }
    }

    /**
     * exercício 2.2.1 - devolve o nome do drawable correspondente a um código meteorológico WMO
     *
     * substitui o enum WMO_WeatherCode e a função getWeatherCodeMap()
     * que estavam em WeatherData.kt
     *
     * em vez de lógica hardcoded em Kotlin, este lê os dados a partir de dois arrays
     * paralelos definidos em res/values/weather_codes.xml:
     *  - R.array.weather_codes - lista de códigos inteiros (ex: 0, 1, 2, 3, ...)
     *  - R.array.weather_images - nome base do drawable correspondente (ex: "clear_", "rain")
     *
     * para os códigos 0, 1 e 2 (céu limpo / parcialmente nublado), o nome da imagem
     * tem sufixo "day" ou "night" consoante a variável [day]
     *
     * @param weatherCode Código WMO recebido da API
     * @return Nome do drawable a carregar, ou null se o código não existir no XML
     */
    private fun getWeatherImage(weatherCode: Int): String? {
        val codes = resources.getIntArray(R.array.weather_codes)
        val images = resources.getStringArray(R.array.weather_images)

        val index = codes.indexOfFirst { it == weatherCode }
        if (index == -1) return null

        val baseImage = images[index]

        // códigos que têm variante dia/noite
        val dayNightCodes = intArrayOf(0, 1, 2)
        return if (weatherCode in dayNightCodes) {
            baseImage + if (day) "day" else "night"
        } else {
            baseImage
        }
    }
}