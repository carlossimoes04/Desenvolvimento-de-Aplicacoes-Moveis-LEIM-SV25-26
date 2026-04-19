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

@Composable
fun WeatherUI(weatherViewModel: WeatherViewModel = viewModel()) {
    val weatherUIState by weatherViewModel.uiState.collectAsState()
    val latitude = weatherUIState.latitude
    val longitude = weatherUIState.longitude
    val temperature = weatherUIState.temperature
    val windSpeed = weatherUIState.windspeed
    val windDirection = weatherUIState.winddirection
    val weatherCode = weatherUIState.weathercode
    val seaLevelPressure = weatherUIState.seaLevelPressure
    val time = weatherUIState.time

    val configuration = LocalConfiguration.current

    val day = true // Must change this in the future
    val mapt = getWeatherCodeMap()
    val wCode = mapt.get(weatherCode)
    val wImage = when (wCode) {
        WMO_WeatherCode.CLEAR_SKY,
        WMO_WeatherCode.MAINLY_CLEAR,
        WMO_WeatherCode.PARTLY_CLOUDY -> if (day) wCode?.image + "day"
            else wCode?.image + "night"
        else -> wCode?.image
    }

    val context = LocalContext.current
    val wIcon = context.resources.getIdentifier(wImage, "drawable", context.packageName)

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
            },
            onLongitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
            },
            onUpdateButtonClick = {
                weatherViewModel.fetchWeather()
            }
        )
    } else {
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
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
            },
            onLongitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
            },
            onUpdateButtonClick = {
                weatherViewModel.fetchWeather()
            }
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
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (wIcon != 0) {
            Image(
                painter = painterResource(id = wIcon),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(120.dp)
            )
        }

        CoordinatesCard(
            latitude = latitude,
            longitude = longitude,
            onLatitudeChange = onLatitudeChange,
            onLongitudeChange = onLongitudeChange
        )

        WeatherCard(
            temperature = temperature,
            windSpeed = windSpeed,
            windDirection = windDirection,
            seaLevelPressure = seaLevelPressure,
            time = time
        )

        Button(
            onClick = onUpdateButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Update Weather", fontSize = 16.sp)
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
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (wIcon != 0) {
                Image(
                    painter = painterResource(id = wIcon),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(80.dp)
                )
            }

            CoordinatesCard(
                latitude = latitude,
                longitude = longitude,
                onLatitudeChange = onLatitudeChange,
                onLongitudeChange = onLongitudeChange,
                modifier = Modifier.weight(1f)
            )

            WeatherCard(
                temperature = temperature,
                windSpeed = windSpeed,
                windDirection = windDirection,
                seaLevelPressure = seaLevelPressure,
                time = time,
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = onUpdateButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Update Weather", fontSize = 16.sp)
        }
    }
}