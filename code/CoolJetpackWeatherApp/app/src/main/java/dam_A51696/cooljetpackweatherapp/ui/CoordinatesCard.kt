package dam_A51696.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CoordinatesCard(
    latitude: Float,
    longitude: Float,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var latitudeText by remember(latitude) { mutableStateOf(latitude.toString()) }
    var longitudeText by remember(longitude) { mutableStateOf(longitude.toString()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Coordinates",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = latitudeText,
                onValueChange = {
                    latitudeText = it
                    onLatitudeChange(it)
                },
                label = { Text("Latitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = longitudeText,
                onValueChange = {
                    longitudeText = it
                    onLongitudeChange(it)
                },
                label = { Text("Longitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}