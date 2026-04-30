package dam_A51696.cooljetpackweatherapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dam_A51696.cooljetpackweatherapp.ui.FavoriteLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "favorites")

object FavoritesRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val FAVORITES_KEY = stringPreferencesKey("favorites_list")

    suspend fun saveFavorites(context: Context, favorites: List<FavoriteLocation>) {
        val encoded = json.encodeToString(favorites) // List -> JSON string
        context.dataStore.edit { prefs ->
            prefs[FAVORITES_KEY] = encoded
        }
    }

    fun loadFavorites(context: Context): Flow<List<FavoriteLocation>> {
        return context.dataStore.data.map { prefs ->
            val raw = prefs[FAVORITES_KEY] ?: return@map emptyList()
            json.decodeFromString(raw) // JSON string -> List
        }
    }
}