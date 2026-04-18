package dam_A51696.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeatherCard(
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
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
            WeatherRow(label = "Sea Level Pressure", value = "$seaLevelPressure hPa")
            WeatherRow(label = "Wind Direction",     value = "$windDirection°")
            WeatherRow(label = "Wind Speed",         value = "$windSpeed km/h")
            WeatherRow(label = "Temperature",        value = "$temperature°C")
            WeatherRow(label = "Time",               value = time)
        }
    }
}