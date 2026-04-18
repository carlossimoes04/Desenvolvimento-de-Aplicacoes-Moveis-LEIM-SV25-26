package dam_A51696.cooljetpackweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dam_A51696.cooljetpackweatherapp.ui.WeatherUI
import dam_A51696.cooljetpackweatherapp.ui.theme.CoolJetpackWeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoolJetpackWeatherAppTheme {
                WeatherUI()
            }
        }
    }
}