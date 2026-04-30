package dam_A51696.cooljetpackweatherapp.ui

data class FavoriteLocation( // data class criada para guardar as informações acerca de um lugar favorito
    val name: String,
    val latitude: Float,
    val longitude: Float
)

data class WeatherUIState(
    val latitude: Float = 38.7223f,
    val longitude: Float = -9.1393f,
    val temperature: Float = 0f,
    val windspeed: Float = 0f,
    val winddirection: Float = 0f,
    val weathercode: Int = 0,
    val seaLevelPressure: Float = 0f,
    val time: String = "",
    val favorites: List<FavoriteLocation> = emptyList(), // lista de lugares favoritos
    val isDay: Boolean = true // indica se o tempo é dia ou noite
)