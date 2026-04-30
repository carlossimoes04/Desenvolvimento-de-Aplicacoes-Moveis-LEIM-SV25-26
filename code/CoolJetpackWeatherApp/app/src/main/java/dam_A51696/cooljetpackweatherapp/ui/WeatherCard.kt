package dam_A51696.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import dam_A51696.cooljetpackweatherapp.R

@Composable
fun WeatherCard(
    temperature: Float,
    windSpeed: Float,
    windDirection: Float,
    seaLevelPressure: Float,
    time: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            WeatherRow(label = stringResource(id = R.string.sea_level_pressure), value = "$seaLevelPressure hPa")
            WeatherRow(label = stringResource(id = R.string.wind_direction),     value = "$windDirection°")
            WeatherRow(label = stringResource(id = R.string.wind_speed),         value = "$windSpeed km/h")
            WeatherRow(label = stringResource(id = R.string.temperature),        value = "$temperature°C")
            WeatherRow(label = stringResource(id = R.string.time),               value = time)
        }
    }
}