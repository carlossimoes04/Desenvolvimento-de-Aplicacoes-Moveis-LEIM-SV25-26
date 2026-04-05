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

class MainActivity : AppCompatActivity() {

    // verifica se é de dia ou de noite para escolher o tema adequado
    // é considerado dia entre as 7h e as 20h, e noite fora desse intervalo
    private val day: Boolean
        get() = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) in 7..20

    override fun onCreate(savedInstanceState: Bundle?) {

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

        fetchWeatherData(38.076f, -9.12f).start()

        val updateButton = findViewById<Button>(R.id.updateButton)
        val latInput = findViewById<EditText>(R.id.latitudeValue)
        val lonInput = findViewById<EditText>(R.id.longitudeValue)

        updateButton.setOnClickListener {
            val lat = latInput.text.toString().toFloatOrNull() ?: 38.076f
            val lon = lonInput.text.toString().toFloatOrNull() ?: -9.12f
            fetchWeatherData(lat, lon).start()
        }
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

            val mapt = getWeatherCodeMap() ;
            val wCode = mapt.get(request.current_weather.weathercode)
            val wImage = when (wCode) {
                WMO_WeatherCode.CLEAR_SKY,
                WMO_WeatherCode.MAINLY_CLEAR,
                WMO_WeatherCode.PARTLY_CLOUDY -> if (day) wCode.image + "day" else wCode.image + "night"
                else -> wCode?.image
            }

            val resID = resources.getIdentifier(wImage, "drawable", packageName)
            if (resID != 0) {
                weatherImage.setImageDrawable(getDrawable(resID))
            }
        }
    }
}